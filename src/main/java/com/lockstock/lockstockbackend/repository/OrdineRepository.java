package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Ordine;
import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    // storico ordini dell'utente (tutti i suoi portafogli), filtro facoltativo per stato, paginato
    @Query(value = "SELECT o FROM Ordine o JOIN FETCH o.titolo " +
            "WHERE o.portafoglio.utente.id = :utenteId " +
            "AND (:stato IS NULL OR o.stato = :stato)",
            countQuery = "SELECT COUNT(o) FROM Ordine o " +
                    "WHERE o.portafoglio.utente.id = :utenteId " +
                    "AND (:stato IS NULL OR o.stato = :stato)")
    Page<Ordine> findOrdiniUtente(@Param("utenteId") Long utenteId, @Param("stato") StatoOrdine stato, Pageable pageable);

    // FOR UPDATE sull'ordine: annullamento e scheduler, dopo il lock si ricontrolla che sia ancora PENDING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Ordine o WHERE o.id = :id")
    Optional<Ordine> findByIdPerAggiornamento(@Param("id") Long id);

    // id dei LIMIT in attesa eseguibili a questo prezzo (BUY: limite >= prezzo, SELL: limite <= prezzo), dal più vecchio
    @Query("SELECT o.id FROM Ordine o " +
            "WHERE o.titolo.id = :titoloId " +
            "AND o.stato = com.lockstock.lockstockbackend.entity.enums.StatoOrdine.PENDING " +
            "AND o.tipoOrdine = com.lockstock.lockstockbackend.entity.enums.TipoOrdine.LIMIT " +
            "AND ((o.tipoOperazione = com.lockstock.lockstockbackend.entity.enums.TipoOperazione.BUY  AND o.prezzoLimite >= :prezzo) " +
            "  OR (o.tipoOperazione = com.lockstock.lockstockbackend.entity.enums.TipoOperazione.SELL AND o.prezzoLimite <= :prezzo)) " +
            "ORDER BY o.dataInserimento")
    List<Long> findIdLimitEseguibili(@Param("titoloId") Long titoloId, @Param("prezzo") BigDecimal prezzo);
}
