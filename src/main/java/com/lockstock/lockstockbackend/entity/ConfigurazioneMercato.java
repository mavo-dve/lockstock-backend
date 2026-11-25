package com.lockstock.lockstockbackend.entity;

import com.lockstock.lockstockbackend.entity.enums.StatoManuale;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name="m_configurazione_mercato")
public class ConfigurazioneMercato {

    public static final Integer ID_UNICO=1;

    @Id
    private Integer id;

    @Column(name="ora_apertura", nullable = false)
    private LocalTime oraApertura;

    @Column(name="ora_chiusura", nullable = false)
    private LocalTime oraChiusura;

    @Column(name="aperto_lunedi", nullable = false)
    private Boolean apertoLunedi;

    @Column(name="aperto_martedi", nullable = false)
    private Boolean apertoMartedi;

    @Column(name="aperto_mercoledi", nullable = false)
    private Boolean apertoMercoledi;

    @Column(name="aperto_giovedi", nullable = false)
    private Boolean apertoGiovedi;

    @Column(name="aperto_venerdi", nullable = false)
    private Boolean apertoVenerdi;

    @Column(name="aperto_sabato", nullable = false)
    private Boolean apertoSabato;

    @Column(name="aperto_domenica", nullable = false)
    private Boolean apertoDomenica;

    @Enumerated(EnumType.STRING)
    @Column(name="stato_manuale", nullable = false)
    private StatoManuale statoManuale;

    @Column(name="commissione_fissa", nullable = false, precision = 9, scale = 2)
    private BigDecimal commissioneFissa;

    @Column(name="saldo_iniziale", nullable = false, precision = 9,  scale = 2)
    private BigDecimal saldoIniziale;

    protected ConfigurazioneMercato(){}

    public void setOraApertura(LocalTime oraApertura) {
        this.oraApertura = oraApertura;
    }

    public void setOraChiusura(LocalTime oraChiusura) {
        this.oraChiusura = oraChiusura;
    }

    public void setApertoLunedi(Boolean apertoLunedi) {
        this.apertoLunedi = apertoLunedi;
    }

    public void setApertoMartedi(Boolean apertoMartedi) {
        this.apertoMartedi = apertoMartedi;
    }

    public void setApertoMercoledi(Boolean apertoMercoledi) {
        this.apertoMercoledi = apertoMercoledi;
    }

    public void setApertoGiovedi(Boolean apertoGiovedi) {
        this.apertoGiovedi = apertoGiovedi;
    }

    public void setApertoVenerdi(Boolean apertoVenerdi) {
        this.apertoVenerdi = apertoVenerdi;
    }

    public void setApertoSabato(Boolean apertoSabato) {
        this.apertoSabato = apertoSabato;
    }

    public void setApertoDomenica(Boolean apertoDomenica) {
        this.apertoDomenica = apertoDomenica;
    }

    public void setStatoManuale(StatoManuale statoManuale) {
        this.statoManuale = statoManuale;
    }

    public void setCommissioneFissa(BigDecimal commissioneFissa) {
        this.commissioneFissa = commissioneFissa;
    }

    public void setSaldoIniziale(BigDecimal saldoIniziale) {
        this.saldoIniziale = saldoIniziale;
    }

    public Integer getId() {
        return id;
    }

    public LocalTime getOraApertura() {
        return oraApertura;
    }

    public LocalTime getOraChiusura() {
        return oraChiusura;
    }

    public Boolean getApertoLunedi() {
        return apertoLunedi;
    }

    public Boolean getApertoMartedi() {
        return apertoMartedi;
    }

    public Boolean getApertoMercoledi() {
        return apertoMercoledi;
    }

    public Boolean getApertoGiovedi() {
        return apertoGiovedi;
    }

    public Boolean getApertoVenerdi() {
        return apertoVenerdi;
    }

    public Boolean getApertoSabato() {
        return apertoSabato;
    }

    public Boolean getApertoDomenica() {
        return apertoDomenica;
    }

    public StatoManuale getStatoManuale() {
        return statoManuale;
    }

    public BigDecimal getCommissioneFissa() {
        return commissioneFissa;
    }

    public BigDecimal getSaldoIniziale() {
        return saldoIniziale;
    }
}
