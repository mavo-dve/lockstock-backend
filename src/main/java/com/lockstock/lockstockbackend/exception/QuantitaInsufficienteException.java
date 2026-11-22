package com.lockstock.lockstockbackend.exception;

public class QuantitaInsufficienteException extends RuntimeException {

    private final int quantitaRichiesta;
    private final int quantitaDisponibile;

    public QuantitaInsufficienteException(int quantitaRichiesta, int quantitaDisponibile) {
        super("Quantità non disponibile: richieste "+ quantitaRichiesta + " azioni, disponibili "+quantitaDisponibile+".");
        this.quantitaRichiesta=quantitaRichiesta;
        this.quantitaDisponibile=quantitaDisponibile;
    }

    public int getQuantitaRichiesta() {
        return quantitaRichiesta;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }
}
