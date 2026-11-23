package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.entity.Portafoglio;
import com.lockstock.lockstockbackend.entity.Posizione;
import com.lockstock.lockstockbackend.exception.RisorsaNonTrovataException;
import com.lockstock.lockstockbackend.repository.PortafoglioRepository;
import com.lockstock.lockstockbackend.repository.PosizioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortafoglioService {

    private final PortafoglioRepository portafoglioRepository;
    private final PosizioneRepository posizioneRepository;
    private final UtenteCorrenteService utenteCorrenteService; // un service può usarne un altro

    public PortafoglioService(PortafoglioRepository portafoglioRepository, PosizioneRepository posizioneRepository, UtenteCorrenteService utenteCorrenteService){
        this.portafoglioRepository=portafoglioRepository;
        this.posizioneRepository=posizioneRepository;
        this.utenteCorrenteService=utenteCorrenteService;
    }

    @Transactional(readOnly = true)
    public List<Portafoglio> getMieiPortafogli(){
        Long idUtente = utenteCorrenteService.getIdUtenteCorrente();
        return portafoglioRepository.findByUtenteId(idUtente);
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
    public List<Posizione> getPosizioni(Long idPortafoglio){
        getMioPortafoglio(idPortafoglio);
        return posizioneRepository.findByPortafoglioId(idPortafoglio);
    }
}
