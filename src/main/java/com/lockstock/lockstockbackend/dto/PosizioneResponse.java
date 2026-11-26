package com.lockstock.lockstockbackend.dto;

import java.math.BigDecimal;

public record PosizioneResponse(
        Long idTitolo,
        String ticker,
        String nomeTitolo,
        Integer quantita,
        BigDecimal prezzoMedioCarico,
        BigDecimal quotazione,
        BigDecimal valoreAttuale,   // quotazione x quantità
        BigDecimal risultato    // (quotazione - pmc) x quantita
) {}
