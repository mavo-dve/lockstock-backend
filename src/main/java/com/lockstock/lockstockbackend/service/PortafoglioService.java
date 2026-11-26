package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.dto.CreaPortafoglioRequest;
import com.lockstock.lockstockbackend.dto.PortafoglioResponse;
import com.lockstock.lockstockbackend.dto.PosizioneResponse;
import com.lockstock.lockstockbackend.entity.Portafoglio;
import com.lockstock.lockstockbackend.entity.Posizione;
import com.lockstock.lockstockbackend.entity.Titolo;
import com.lockstock.lockstockbackend.entity.Utente;
import com.lockstock.lockstockbackend.exception.RisorsaNonTrovataException;
import com.lockstock.lockstockbackend.repository.PortafoglioRepository;
import com.lockstock.lockstockbackend.repository.PosizioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PortafoglioService {

    private final PortafoglioRepository portafoglioRepository;
    private final PosizioneRepository posizioneRepository;
    private final UtenteCorrenteService utenteCorrenteService; // un service può usarne un altro
    private final MercatoService mercatoService;


    public PortafoglioService(PortafoglioRepository portafoglioRepository, PosizioneRepository posizioneRepository, UtenteCorrenteService utenteCorrenteService, MercatoService mercatoService){
        this.portafoglioRepository=portafoglioRepository;
        this.posizioneRepository=posizioneRepository;
        this.utenteCorrenteService=utenteCorrenteService;
        this.mercatoService=mercatoService;
    }

    @Transactional(readOnly = true)
    public List<PortafoglioResponse> getMieiPortafogli(){
        Long idUtente = utenteCorrenteService.getIdUtenteCorrente();
        return portafoglioRepository.findByUtenteId(idUtente).stream().map(this::toResponse).toList();
    }

    // il portafoglio solo se è dell'utente corrente; altrimenti "non trovato" (anche se esiste)
    @Transactional(readOnly = true)
    public Portafoglio getMioPortafoglio(Long idPortafoglio){
        Long idUtente = utenteCorrenteService.getIdUtenteCorrente();
        return portafoglioRepository.findByIdAndUtenteId(idPortafoglio, idUtente).orElseThrow(
                () -> new RisorsaNonTrovataException("Portafoglio "+idPortafoglio+" non trovato")
        );
    }

    @Transactional(readOnly = true)
    public List<PosizioneResponse> getPosizioni(Long idPortafoglio){
        getMioPortafoglio(idPortafoglio);
        return posizioneRepository.findByPortafoglioIdConTitolo(idPortafoglio).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PortafoglioResponse getPortafoglio(Long idPortafoglio){
        return toResponse(getMioPortafoglio(idPortafoglio));
    }

    @Transactional
    public PortafoglioResponse creaPortafoglio(CreaPortafoglioRequest richiesta){
        Utente utente=utenteCorrenteService.getUtenteCorrente();
        BigDecimal saldoIniziale = mercatoService.getSaldoIniziale();
        Portafoglio portafoglio=new Portafoglio(generaNumero(), utente);
        portafoglio.setNome(richiesta.nome());
        portafoglio.setSaldo(saldoIniziale);
        return toResponse(portafoglioRepository.save(portafoglio));
    }


    //metodi privati

    private PortafoglioResponse toResponse(Portafoglio p){
        return new PortafoglioResponse(p.getId(), p.getNome(), p.getNumero(), p.getSaldo(), p.getCreated());
    }

    private PosizioneResponse toResponse(Posizione pos){
        Titolo t = pos.getTitolo();
        BigDecimal qta = BigDecimal.valueOf(pos.getQuantitaTotale());
        BigDecimal valoreAttuale = t.getQuotazione().multiply(qta);
        BigDecimal risultato = t.getQuotazione().subtract(pos.getPrezzoMedioCarico()).multiply(qta);
        return new PosizioneResponse(t.getId(), t.getTicker(), t.getNome(), pos.getQuantitaTotale(), pos.getPrezzoMedioCarico(), t.getQuotazione(), valoreAttuale, risultato);
    }

    private String generaNumero(){
        return "PF-"+ UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

}
