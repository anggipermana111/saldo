package com.dss.saldo.repositories;

import com.dss.saldo.models.InvestmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<InvestmentTransaction, Long> {
}
