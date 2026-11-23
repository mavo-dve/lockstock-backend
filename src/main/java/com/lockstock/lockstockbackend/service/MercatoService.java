package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.exception.MercatoChiusoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class MercatoService {

    private final LocalTime apertura;
    private final LocalTime chiusura;
    private final ZoneId fuso;

    public MercatoService(@Value("${lockstock.mercato.apertura}") LocalTime apertura, @Value("${lockstock.mercato.chiusura}") LocalTime chiusura, @Value("${lockstock.mercato.fuso}") ZoneId fuso){
        this.apertura=apertura;
        this.chiusura=chiusura;
        this.fuso=fuso;
    }

    //true se adesso (nel fuso configurato) è un giorno feriale dentro l'orario
    public boolean isAperto(){
        LocalDateTime adesso = LocalDateTime.now(fuso);
        return isGiornoLavorativo(adesso.getDayOfWeek())
                && !adesso.toLocalTime().isBefore(apertura) // >= apertura
                && adesso.toLocalTime().isBefore(chiusura); // < chiusura
    }

    // prossima apertura: oggi più tardio, oppure prossimo giorno lavorativo
    public LocalDateTime prossimaApertura(){
        LocalDateTime adesso = LocalDateTime.now(fuso);
        LocalDateTime candidato = adesso.toLocalDate().atTime(apertura);
        if(!candidato.isAfter(adesso)){ //apertua di oggi già passaata
            candidato = candidato.plusDays(1);
        }
        while(!isGiornoLavorativo(candidato.getDayOfWeek())){ // salta sabato e dominca
            candidato=candidato.plusDays(1);
        }
        return candidato;
    }

    // da chiaramare all'inizio di ogni operazione: se è chiuso blocca
    public void verificaAperto(){
        if(!isAperto()){
            String quando = prossimaApertura().format(
                    DateTimeFormatter.ofPattern(
                            "EEEE d MMMM 'alle' HH:mm",
                            java.util.Locale.ITALIAN)
            );
            throw new MercatoChiusoException("Il mercato è chiuso. Riapre "+quando);
        }
    }

    private boolean isGiornoLavorativo(DayOfWeek giorno){
        return giorno != DayOfWeek.SATURDAY && giorno != DayOfWeek.SUNDAY;
    }

}
