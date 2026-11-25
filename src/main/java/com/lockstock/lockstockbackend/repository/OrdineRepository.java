package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Ordine;
import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    // storico ordini dell'utente (tutti i suoi portafogli), filtro facoltativo per stato, paginato
    @Query(value = "SELECT o FROM Ordine o JOIN FETCH o.titolo " +
            "WHERE o.portafoglio.utente.id = :utenteId " +
            "AND (:stato IS NULL OR o.stato = :stato)",
            countQuery = "SELECT COUNT(o) FROM Ordine o " +
                    "WHERE o.portafoglio.utente.id = :utenteId " +
                    "AND (:stato IS NULL OR o.stato = :stato)")
    Page<Ordine> findOrdiniUtente(@Param("utenteId") Long utenteId, @Param("stato") StatoOrdine stato, Pageable pageable);
}
