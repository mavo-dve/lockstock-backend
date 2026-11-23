package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.entity.Utente;
import com.lockstock.lockstockbackend.exception.RisorsaNonTrovataException;
import com.lockstock.lockstockbackend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtenteCorrenteService {

    private final UtenteRepository utenteRepository;
    private final Long idUtenteCorrente;

    public UtenteCorrenteService(UtenteRepository utenteRepository, @Value("${lockstock.utente-corrente-id}")  Long idUtenteCorrente){
        this.utenteRepository=utenteRepository;
        this.idUtenteCorrente=idUtenteCorrente;
    }

    @Transactional(readOnly = true)
    public Utente getUtenteCorrente() {
        return utenteRepository.findById(idUtenteCorrente).orElseThrow(() ->
                new RisorsaNonTrovataException("Utente corrente (id "+idUtenteCorrente+") non trovato")
        );
    }

    public Long getIdUtenteCorrente() {
        return idUtenteCorrente;
    }
}
