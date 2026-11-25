package com.lockstock.lockstockbackend.entity;

import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import com.lockstock.lockstockbackend.entity.enums.TipoOperazione;
import com.lockstock.lockstockbackend.entity.enums.TipoOrdine;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="m_ordine")
public class Ordine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fk_portafoglio", nullable = false)
    private Portafoglio portafoglio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fk_titolo", nullable = false)
    private Titolo titolo;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_operazione", nullable = false)
    private TipoOperazione tipoOperazione;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_ordine", nullable = false)
    private TipoOrdine tipoOrdine;

    @Enumerated(EnumType.STRING)
    @Column(name="stato", nullable = false)
    private StatoOrdine stato = StatoOrdine.PENDING;

    @Column(name="quantita_richiesta", nullable = false)
    private Integer quantitaRichiesta;

    @Column(name="prezzo_limite", nullable = true, precision = 9, scale = 2)
    private BigDecimal prezzoLimite;

    @Column(name="prezzo_medio_eseguito", nullable = true, precision = 9, scale = 2)
    private BigDecimal prezzoMedioEseguito;

    @Column(name="commissioni", nullable = false, precision = 9, scale = 2)
    private BigDecimal commissioni = BigDecimal.ZERO;

    @Column(name="motivo_rifiuto", length = 255)
    private String motivoRifiuto;

    @Column(name="data_inserimento", nullable = false, updatable = false)
    private LocalDateTime dataInserimento;

    @Column(name="data_esecuzione", nullable = true)
    private LocalDateTime dataEsecuzione;

    protected Ordine(){}

    public Ordine(Portafoglio portafoglio, Titolo titolo, TipoOperazione tipoOperazione, TipoOrdine tipoOrdine, Integer quantitaRichiesta, BigDecimal prezzoLimite){
        this.portafoglio=portafoglio;
        this.titolo=titolo;
        this.tipoOperazione=tipoOperazione;
        this.tipoOrdine=tipoOrdine;
        this.quantitaRichiesta=quantitaRichiesta;
        this.prezzoLimite=prezzoLimite;
    }

    @PrePersist
    void onCreate(){
        if(dataInserimento==null) dataInserimento=LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portafoglio getPortafoglio() {
        return portafoglio;
    }

    public void setPortafoglio(Portafoglio portafoglio) {
        this.portafoglio = portafoglio;
    }

    public Titolo getTitolo() {
        return titolo;
    }

    public void setTitolo(Titolo titolo) {
        this.titolo = titolo;
    }

    public TipoOperazione getTipoOperazione() {
        return tipoOperazione;
    }

    public void setTipoOperazione(TipoOperazione tipoOperazione) {
        this.tipoOperazione = tipoOperazione;
    }

    public TipoOrdine getTipoOrdine() {
        return tipoOrdine;
    }

    public void setTipoOrdine(TipoOrdine tipoOrdine) {
        this.tipoOrdine = tipoOrdine;
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public void setStato(StatoOrdine stato) {
        this.stato = stato;
    }

    public Integer getQuantitaRichiesta() {
        return quantitaRichiesta;
    }

    public void setQuantitaRichiesta(Integer quantitaRichiesta) {
        this.quantitaRichiesta = quantitaRichiesta;
    }

    public BigDecimal getPrezzoLimite() {
        return prezzoLimite;
    }

    public void setPrezzoLimite(BigDecimal prezzoLimite) {
        this.prezzoLimite = prezzoLimite;
    }

    public BigDecimal getPrezzoMedioEseguito() {
        return prezzoMedioEseguito;
    }

    public void setPrezzoMedioEseguito(BigDecimal prezzoMedioEseguito) {
        this.prezzoMedioEseguito = prezzoMedioEseguito;
    }

    public BigDecimal getCommissioni() {
        return commissioni;
    }

    public void setCommissioni(BigDecimal commissioni) {
        this.commissioni = commissioni;
    }

    public String getMotivoRifiuto() {
        return motivoRifiuto;
    }

    public void setMotivoRifiuto(String motivoRifiuto) {
        this.motivoRifiuto = motivoRifiuto;
    }

    public LocalDateTime getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(LocalDateTime dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    public LocalDateTime getDataEsecuzione() {
        return dataEsecuzione;
    }

    public void setDataEsecuzione(LocalDateTime dataEsecuzione) {
        this.dataEsecuzione = dataEsecuzione;
    }
}
