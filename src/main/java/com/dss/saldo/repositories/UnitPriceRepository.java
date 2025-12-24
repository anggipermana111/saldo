package com.dss.saldo.repositories;

import com.dss.saldo.models.UnitPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDate;

public interface UnitPriceRepository extends JpaRepository<UnitPrice, Long> {
    Optional<UnitPrice> findByNavDate(LocalDate navDate);
    UnitPrice findTopByOrderByNavDateDesc();
}
