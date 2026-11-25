package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Posizione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PosizioneRepository extends JpaRepository<Posizione, Long> {
    // posizioni del portafoglio con il titolo già caricato (niente N+1), in ordine di nome
    @Query("SELECT p FROM Posizione p JOIN FETCH p.titolo " +
            "WHERE p.portafoglio.id = :portafoglioId " +
            "ORDER BY p.titolo.nome")
    List<Posizione> findByPortafoglioIdConTitolo(@Param("portafoglioId") Long portafoglioId);

    // la posizione da aggiornare dopo un acquisto o una vendita
    Optional<Posizione> findByPortafoglioIdAndTitoloId(Long portafoglioId, Long titoloId);

}
