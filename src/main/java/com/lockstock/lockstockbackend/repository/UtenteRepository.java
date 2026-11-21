package com.lockstock.lockstockbackend.repository;

import com.lockstock.lockstockbackend.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

}
