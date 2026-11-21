package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Portafoglio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortafoglioRepository extends JpaRepository<Portafoglio, Long> {
}
