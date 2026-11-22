package com.lockstock.lockstockbackend.exception;

public class PosizioneInsufficienteException extends RuntimeException {

    private final int quantitaRichiesta;
    private final int quantitaPosseduta;

    public PosizioneInsufficienteException(int quantitaRichiesta, int quantitaPosseduta) {
        super("Non possiedi abbastanza azioni: vuoi venderne "+quantitaRichiesta+", ne hai "+quantitaPosseduta);
        this.quantitaPosseduta=quantitaPosseduta;
        this.quantitaRichiesta=quantitaRichiesta;
    }

    public int getQuantitaRichiesta() {
        return quantitaRichiesta;
    }

    public int getQuantitaPosseduta() {
        return quantitaPosseduta;
    }
}
