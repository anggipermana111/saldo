package com.dss.saldo.services;

import com.dss.saldo.dtos.PortfolioSummaryResponse;
import com.dss.saldo.models.InvestmentTransaction;
import com.dss.saldo.models.UnitPrice;
import com.dss.saldo.repositories.TransactionRepository;
import com.dss.saldo.repositories.UnitPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final TransactionRepository transactionRepo;
    private final UnitPriceRepository priceRepo;

    @Transactional(readOnly = true)
    public PortfolioSummaryResponse calculatePortfolio() {
        List<InvestmentTransaction> transactions = transactionRepo.findAll();
        UnitPrice latestPriceObj = priceRepo.findTopByOrderByNavDateDesc();

        if (transactions.isEmpty() || latestPriceObj == null) {
            return PortfolioSummaryResponse.builder()
                    .currentBalance(BigDecimal.ZERO)
                    .totalInvested(BigDecimal.ZERO)
                    .totalReturn(BigDecimal.ZERO)
                    .performancePercentage(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal latestPrice = latestPriceObj.getPrice();

        BigDecimal totalUnits = transactions.stream()
                .map(InvestmentTransaction::getUnits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentBalance = totalUnits.multiply(latestPrice);

        BigDecimal totalInvested = BigDecimal.ZERO;

        for (InvestmentTransaction trx : transactions) {
            LocalDate hPlusOneDate = trx.getTransactionDate().plusDays(1);

            UnitPrice historicalPrice = priceRepo.findByNavDate(hPlusOneDate)
                    .orElseThrow(() -> new RuntimeException("NAV Price not found for date: " + hPlusOneDate));

            BigDecimal cost = trx.getUnits().multiply(historicalPrice.getPrice());
            totalInvested = totalInvested.add(cost);
        }

        BigDecimal totalReturn = currentBalance.subtract(totalInvested);

        BigDecimal performancePct = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            performancePct = totalReturn.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return PortfolioSummaryResponse.builder()
                .currentBalance(currentBalance)
                .totalInvested(totalInvested)
                .totalReturn(totalReturn)
                .performancePercentage(performancePct)
                .build();
    }
}
