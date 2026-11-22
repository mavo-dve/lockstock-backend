package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Ordine;
import com.lockstock.lockstockbackend.entity.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    List<Ordine> findByPortafoglioIdOrderByDataInserimentoDesc(Long portafoglioId);
    List<Ordine> findByPortafoglioIdAndStatoOrderByDataInserimentoDesc(Long portafoglioId, StatoOrdine stato);
}
