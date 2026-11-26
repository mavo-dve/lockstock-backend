package com.lockstock.lockstockbackend.service;

import com.lockstock.lockstockbackend.entity.ConfigurazioneMercato;
import com.lockstock.lockstockbackend.entity.enums.StatoManuale;
import com.lockstock.lockstockbackend.repository.ConfigurazioneMercatoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
public class MercatoService {

    private final ConfigurazioneMercatoRepository configurazioneRepository;
    private final ZoneId fuso;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("EEEE d MMMM 'alle' HH:mm", Locale.ITALIAN);

    public MercatoService(ConfigurazioneMercatoRepository configurazioneRepository , @Value("${lockstock.mercato.fuso}") ZoneId fuso){
        this.configurazioneRepository=configurazioneRepository;
        this.fuso=fuso;
    }

    @Transactional(readOnly = true)
    public boolean isAperto(){
        return isAperto(leggiConfiurazione(), LocalDateTime.now(fuso));
    }

    @Transactional(readOnly = true)
    public String motivoChiusura(){
        ConfigurazioneMercato conf = leggiConfiurazione();
        if(conf.getStatoManuale() == StatoManuale.FORZATO_CHIUSO){
            return "Mercato chiuso dall'amministratore";
        }
        return prossimaApertura(conf, LocalDateTime.now(fuso)).map(
                quando -> "Mercato chiuso: riapre"+quando.format(FORMATO)).orElse("Mercato chiusp: nessun giorno di apertura configurato");
    }


    private ConfigurazioneMercato leggiConfiurazione(){
        return configurazioneRepository.findById(ConfigurazioneMercato.ID_UNICO).orElseThrow(
                () -> new IllegalStateException("Configurazione del mercato mancante: esegui lo script dei dati iniziali")
        );
    }

    private boolean isAperto(ConfigurazioneMercato conf, LocalDateTime adesso){
        return switch(conf.getStatoManuale()){
            case FORZATO_APERTO -> true;
            case FORZATO_CHIUSO -> false;
            case AUTOMATICO -> isGiornoAperto(conf, adesso.getDayOfWeek()) && !adesso.toLocalTime().isBefore(conf.getOraApertura()) && adesso.toLocalTime().isBefore(conf.getOraChiusura());
        };
    }

    private Optional<LocalDateTime> prossimaApertura(ConfigurazioneMercato conf, LocalDateTime adesso){
        LocalDateTime candidato = adesso.toLocalDate().atTime(conf.getOraApertura());
        if (!candidato.isAfter(adesso)) {
            candidato=candidato.plusDays(1); // oggi è passata
        }
        for(int i=0; i<7; i++){ //al massimo 1 settimana avanti
            if(isGiornoAperto(conf, candidato.getDayOfWeek())){
                return Optional.of(candidato);
            }
            candidato=candidato.plusDays(1);
        }
        return Optional.empty(); //nessun giorno aperto in conf
    }

    private boolean isGiornoAperto(ConfigurazioneMercato conf, DayOfWeek giorno){
        return switch (giorno){
            case MONDAY -> conf.getApertoLunedi();
            case TUESDAY -> conf.getApertoMartedi();
            case WEDNESDAY -> conf.getApertoMercoledi();
            case THURSDAY -> conf.getApertoGiovedi();
            case FRIDAY -> conf.getApertoVenerdi();
            case SATURDAY -> conf.getApertoSabato();
            case SUNDAY -> conf.getApertoDomenica();
        };
    }

}
