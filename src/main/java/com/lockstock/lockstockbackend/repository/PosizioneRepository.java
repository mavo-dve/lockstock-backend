package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Posizione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PosizioneRepository extends JpaRepository<Posizione, Long> {
    List<Posizione> findByPortafoglioId(Long portafoglioId);
    Optional<Posizione> findByPortafoglioIdAndTitoloId(Long portafoglioId, Long titoloId);
}
