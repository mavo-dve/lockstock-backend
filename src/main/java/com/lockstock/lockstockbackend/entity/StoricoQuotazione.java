package com.lockstock.lockstockbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="m_storico_quotazione")
public class StoricoQuotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fk_titolo", nullable = false)
    private Titolo titolo;

    @Column(name="prezzo", nullable = false, precision = 9, scale = 2)
    private BigDecimal prezzo;

    @Column(name="data_ora", nullable = false, updatable = false)
    private LocalDateTime dataOra;

    protected StoricoQuotazione(){}

    public StoricoQuotazione(Titolo titolo, BigDecimal prezzo){
        this.titolo=titolo;
        this.prezzo=prezzo;
    }

    @PrePersist
    void onCreate(){
        if(dataOra==null) dataOra=LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Titolo getTitolo() {
        return titolo;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }
}
