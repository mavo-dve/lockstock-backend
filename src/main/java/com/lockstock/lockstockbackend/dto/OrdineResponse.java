package com.lockstock.lockstockbackend.dto;

import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import com.lockstock.lockstockbackend.entity.enums.TipoOperazione;
import com.lockstock.lockstockbackend.entity.enums.TipoOrdine;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdineResponse(
        Long id,
        Long idTitolo,
        String ticker,
        TipoOperazione tipoOperazione,
        TipoOrdine tipoOrdine,
        StatoOrdine statoOrdine,
        Integer quantita,
        BigDecimal prezzoLimite,
        BigDecimal prezzoEseguito,
        BigDecimal commissioni,
        String motivoRifiuto,
        LocalDateTime dataInserimento,
        LocalDateTime dataEsecuzione
) {}
