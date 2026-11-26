package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.StoricoQuotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StoricoQuotazioneRepository extends JpaRepository<StoricoQuotazione, Long> {

    //storicp di un titolo in un intervallo, dal più vecchio (per il grafico)
    @Query("SELECT s FROM StoricoQuotazione s " +
            "WHERE s.titolo.id = :titoloId AND s.dataOra BETWEEN :da AND :a " +
            "ORDER BY s.dataOra")
    List<StoricoQuotazione> findStorico(@Param("titoloId") Long titoloId, @Param("da")LocalDateTime da, @Param("a") LocalDateTime a);

}