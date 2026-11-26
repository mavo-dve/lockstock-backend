package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Portafoglio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PortafoglioRepository extends JpaRepository<Portafoglio, Long> {

    List<Portafoglio> findByUtenteId(Long utenteId);
    Optional<Portafoglio> findByIdAndUtenteId(Long id, Long utenteId);
    boolean existsByNumero(String numero);

    // blocca il portafoglio dell'utente (saldo), da prendere dopo il lock sul titolo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Portafoglio p WHERE p.id = :id AND p.utente.id = :utenteId")
    Optional<Portafoglio> findByIdAndUtenteIdPerAggiornamento(@Param("id") Long id, @Param("utenteId") Long utenteId);
}
