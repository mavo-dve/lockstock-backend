package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Titolo;
import com.lockstock.lockstockbackend.entity.enums.Valuta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TitoloRepository extends JpaRepository<Titolo, Long> {

    Optional<Titolo> findByTicker(String ticker);
    boolean existsByTicker(String ticker);
    boolean existsByIsin(String isin);
    List<Titolo> findByNomeContainingIgnoreCase(String testo);
    List<Titolo> findByValuta(Valuta valuta);

}
