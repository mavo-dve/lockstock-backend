package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {
}
