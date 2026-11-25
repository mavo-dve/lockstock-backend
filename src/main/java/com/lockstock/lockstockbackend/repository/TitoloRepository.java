package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Titolo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TitoloRepository extends JpaRepository<Titolo, Long> {

    Optional<Titolo> findByTicker(String ticker);

    @Query("SELECT t FROM Titolo t " +
            "WHERE LOWER(t.nome)   LIKE LOWER(CONCAT('%', :testo, '%')) " +
            "   OR LOWER(t.ticker) LIKE LOWER(CONCAT('%', :testo, '%')) " +
            "   OR LOWER(t.isin)   LIKE LOWER(CONCAT('%', :testo, '%')) " +
            "ORDER BY t.nome")
    List<Titolo> cerca(@Param("testo") String testo);

    //blocca il titolo fino al commit, gli altri aspettano (va usato dentro @Transactional)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Titolo t WHERE t.id = :id")
    Optional<Titolo> findByIdPerAggiornamento(@Param("id") Long id);

}
