package com.lockstock.lockstockbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortafoglioResponse(
        Long id,
        String nome,
        String numero,
        BigDecimal saldo,
        LocalDateTime created
) {}


