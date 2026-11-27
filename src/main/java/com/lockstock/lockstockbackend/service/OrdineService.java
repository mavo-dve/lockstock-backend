package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.dto.OrdineResponse;
import com.lockstock.lockstockbackend.entity.Ordine;
import com.lockstock.lockstockbackend.entity.Portafoglio;
import com.lockstock.lockstockbackend.entity.Posizione;
import com.lockstock.lockstockbackend.entity.Titolo;
import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import com.lockstock.lockstockbackend.entity.enums.TipoOperazione;
import com.lockstock.lockstockbackend.entity.enums.TipoOrdine;
import com.lockstock.lockstockbackend.exception.OrdineNonValidoException;
import com.lockstock.lockstockbackend.exception.RisorsaNonTrovataException;
import com.lockstock.lockstockbackend.repository.OrdineRepository;
import com.lockstock.lockstockbackend.repository.PortafoglioRepository;
import com.lockstock.lockstockbackend.repository.PosizioneRepository;
import com.lockstock.lockstockbackend.repository.TitoloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrdineService {

    final private TitoloRepository titoloRepository;
    final private PortafoglioRepository portafoglioRepository;
    final private PosizioneRepository posizioneRepository;
    final private OrdineRepository ordineRepository;
    final private MercatoService mercatoService;
    final private UtenteCorrenteService utenteCorrenteService;

    public OrdineService(TitoloRepository titoloRepository, PortafoglioRepository portafoglioRepository, PosizioneRepository posizioneRepository, OrdineRepository ordineRepository, MercatoService mercatoService, UtenteCorrenteService utenteCorrenteService){
        this.titoloRepository=titoloRepository;
        this.portafoglioRepository=portafoglioRepository;
        this.posizioneRepository=posizioneRepository;
        this.ordineRepository=ordineRepository;
        this.mercatoService=mercatoService;
        this.utenteCorrenteService=utenteCorrenteService;
    }

    // acquisto MARKET BUY, tutto in una transazione
    @Transactional
    public OrdineResponse acquista(Long idPortafoglio, Long idTitolo, int quantita) {
        //valida input
        if (quantita <= 0) {
            throw new OrdineNonValidoException("La quantità deve essere maggiore di 0");
        }
        Long idUtente = utenteCorrenteService.getIdUtenteCorrente();

        //LOCK sul Titolo
        Titolo titolo = titoloRepository.findByIdPerAggiornamento(idTitolo).orElseThrow(
                () -> new RisorsaNonTrovataException("Titolo "+idTitolo+" non trovato")
        );

        // LOCK Portafoglio + controlo proprietà
        Portafoglio portafoglio=portafoglioRepository.findByIdAndUtenteIdPerAggiornamento(idPortafoglio, idUtente).orElseThrow(
                () -> new RisorsaNonTrovataException("Portafoglio "+idPortafoglio+ " non trovato")
        );

        //PENDING
        Ordine ordine = new Ordine(portafoglio, titolo, TipoOperazione.BUY, TipoOrdine.MARKET, quantita, null);

        //controlli secondari dopo il LOCK, rifiuta con REJECTED
        if(!mercatoService.isAperto()){
            return rifiuta(ordine, mercatoService.motivoChiusura());
        }
        if(titolo.getQuantitaDisponibile() < quantita){
            return rifiuta(ordine, "Quantità non disponibile: richiesti "+quantita+ " titoli disponibili "+titolo.getQuantitaDisponibile());
        }
        BigDecimal quotazione = titolo.getQuotazione();
        BigDecimal commissioni = mercatoService.getCommissioneFissa();
        BigDecimal totale = quotazione.multiply(BigDecimal.valueOf(quantita)).add(commissioni);
        if(portafoglio.getSaldo().compareTo(totale)<0){
            return rifiuta(ordine, "Saldo insufficiente: servono "+ totale+ "€, disponibili "+portafoglio.getSaldo()+"€");
        }

        //update automatico al commit
        titolo.setQuantitaDisponibile(titolo.getQuantitaDisponibile()-quantita);
        portafoglio.setSaldo(portafoglio.getSaldo().subtract(totale));
        aggiornaPosizione(portafoglio, titolo, quantita, quotazione);

        //ordine FILLED salvato
        ordine.setStato(StatoOrdine.FILLED);
        ordine.setPrezzoMedioEseguito(quotazione);
        ordine.setCommissioni(commissioni);
        ordine.setDataEsecuzione(LocalDateTime.now());
        return toResponse(ordineRepository.save(ordine));

    }

    //vendita
    @Transactional
    public OrdineResponse vendi(Long idPortafoglio, Long idTitolo, int quantita){
        if(quantita<=0){
            throw new OrdineNonValidoException("La quantità deve essere maggiore di zero");
        }

        Long idUtente = utenteCorrenteService.getIdUtenteCorrente();

        Titolo titolo = titoloRepository.findByIdPerAggiornamento(idTitolo).orElseThrow(
                () -> new RisorsaNonTrovataException("Titolo non trovato "+idTitolo+" non trovato")
        );

        Portafoglio portafoglio= portafoglioRepository.findByIdAndUtenteIdPerAggiornamento(idPortafoglio, idUtente).orElseThrow(
                () -> new RisorsaNonTrovataException("Portafoglio "+idPortafoglio+" non trovato")
        );

        Ordine ordineVenduto = new Ordine(portafoglio, titolo, TipoOperazione.SELL, TipoOrdine.MARKET, quantita, null);

        if(!mercatoService.isAperto()){
            return rifiuta(ordineVenduto, mercatoService.motivoChiusura());
        }
        Optional<Posizione> esistente = posizioneRepository.findByPortafoglioIdAndTitoloId(idPortafoglio, idTitolo);
        int posseduti = esistente.map(Posizione::getQuantitaTotale).orElse(0);
        if(posseduti<quantita){
            return rifiuta(ordineVenduto,"Non possiedi abbastanza titoli: vuoi vendere "+ quantita + ", ne hai "+ posseduti);
        }
        BigDecimal quotazione = titolo.getQuotazione();
        BigDecimal commissioni = mercatoService.getCommissioneFissa();
        BigDecimal saldoVendita = quotazione.multiply(BigDecimal.valueOf(quantita)).subtract(commissioni);

        if(portafoglio.getSaldo().add(saldoVendita).compareTo(BigDecimal.ZERO) < 0){
            return rifiuta(ordineVenduto, "Saldo insufficiente per pagare le commissioni");
        }

        titolo.setQuantitaDisponibile(titolo.getQuantitaDisponibile() + quantita);
        portafoglio.setSaldo(portafoglio.getSaldo().add(saldoVendita));
        Posizione pos = esistente.get();
        int rimasti = posseduti-quantita;
        if (rimasti == 0) {
            posizioneRepository.delete(pos);
        }else{
            pos.setQuantitaTotale(rimasti);
        }

        ordineVenduto.setStato(StatoOrdine.FILLED);
        ordineVenduto.setPrezzoMedioEseguito(quotazione);
        ordineVenduto.setCommissioni(commissioni);
        ordineVenduto.setDataEsecuzione(LocalDateTime.now());
        return toResponse(ordineRepository.save(ordineVenduto));

    }


    //riufiuta + motivo
    private OrdineResponse rifiuta(Ordine ordine, String motivo){
        ordine.setStato(StatoOrdine.REJECTED);
        ordine.setMotivoRifiuto(motivo);
        return toResponse(ordineRepository.save(ordine));
    }

    //posizione nuova
    private void aggiornaPosizione(Portafoglio portafoglio, Titolo titolo, int quantita, BigDecimal prezzo){
        Optional<Posizione> esistente = posizioneRepository.findByPortafoglioIdAndTitoloId(portafoglio.getId(), titolo.getId());
        if (esistente.isEmpty()){
           posizioneRepository.save(new Posizione(portafoglio, titolo, quantita, prezzo));
           return;
        }
        Posizione pos = esistente.get();
        int nuovaQta = pos.getQuantitaTotale()+quantita;
        BigDecimal nuovoPMC = pos.getPrezzoMedioCarico().multiply(BigDecimal.valueOf(pos.getQuantitaTotale())).add(prezzo.multiply(BigDecimal.valueOf(quantita))).divide(BigDecimal.valueOf(nuovaQta),2, RoundingMode.HALF_UP);
        pos.setQuantitaTotale(nuovaQta);
        pos.setPrezzoMedioCarico(nuovoPMC);
    }

    private OrdineResponse toResponse(Ordine o){
        Titolo t = o.getTitolo();
        return new OrdineResponse(o.getId(), t.getId(), t.getTicker(), o.getTipoOperazione(), o.getTipoOrdine(), o.getStato(), o.getQuantitaRichiesta(), o.getPrezzoLimite(), o.getPrezzoMedioEseguito(), o.getCommissioni(), o.getMotivoRifiuto(), o.getDataInserimento(), o.getDataEsecuzione());
    }
}
