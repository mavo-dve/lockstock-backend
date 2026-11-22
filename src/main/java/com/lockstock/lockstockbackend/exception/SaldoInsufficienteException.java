package com.lockstock.lockstockbackend.exception;

import java.math.BigDecimal;

public class SaldoInsufficienteException extends RuntimeException {

    private final BigDecimal importoRichiesto;
    private final BigDecimal saldoDisponibile;

    public SaldoInsufficienteException(BigDecimal importoRichiesto, BigDecimal saldoDisponibile) {
      super("Saldo insufficiente: servono "+importoRichiesto+", disponibili "+saldoDisponibile);
      this.importoRichiesto=importoRichiesto;
      this.saldoDisponibile=saldoDisponibile;
    }

  public BigDecimal getImportoRichiesto() {
    return importoRichiesto;
  }

  public BigDecimal getSaldoDisponibile() {
    return saldoDisponibile;
  }
}
