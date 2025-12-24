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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final TransactionRepository transactionRepo;
    private final UnitPriceRepository priceRepo;

    @Transactional(readOnly = true)
    public PortfolioSummaryResponse calculatePortfolio() {
        // Ambil semua transaksi
        List<InvestmentTransaction> transactions = transactionRepo.findAll();
        // Ambil semua kolom harga unit terakhir
        UnitPrice latestPriceObj = priceRepo.findTopByOrderByNavDateDesc();

        // Handle null, return zero
        if (transactions.isEmpty() || latestPriceObj == null) {
            return PortfolioSummaryResponse.builder()
                    .currentBalance(BigDecimal.ZERO)
                    .totalInvested(BigDecimal.ZERO)
                    .totalReturn(BigDecimal.ZERO)
                    .performancePercentage(BigDecimal.ZERO)
                    .build();
        }

        // Ambil kolom price saja
        BigDecimal latestPrice = latestPriceObj.getPrice();

        // Hitung jumlah unit
        BigDecimal totalUnits = transactions.stream()
                .map(InvestmentTransaction::getUnits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saldo sekarang
        BigDecimal currentBalance = totalUnits.multiply(latestPrice);

        // Total investasi, deklarasi 0
        BigDecimal totalInvested = BigDecimal.ZERO;

        // Hitung total transaksi dari list transaksi
        for (InvestmentTransaction trx : transactions) {
            // Mengambil tanggal transaksi + 1 hari
            LocalDate hPlusOneDate = trx.getTransactionDate().plusDays(1);

            // Mengambil harga unit di tanggal tertentu, throw error jika kosong, jika tanggal kosong ambil tanggal setelahnya

            Optional<UnitPrice> historicalPriceOpt = priceRepo.findByNavDate(hPlusOneDate);
            UnitPrice historicalPrice = new UnitPrice();
            while(historicalPriceOpt.isEmpty()) {
                hPlusOneDate = hPlusOneDate.plusDays(1);
                historicalPriceOpt = priceRepo.findByNavDate(hPlusOneDate);
            }
            historicalPrice = historicalPriceOpt.get();

            // jumlah unit transaksi dikali harga sesuai tanggal
            BigDecimal cost = trx.getUnits().multiply(historicalPrice.getPrice());

            // ditambahkan ke total investasi
            totalInvested = totalInvested.add(cost);
        }

        // method subtract untuk pengurangan
        BigDecimal totalReturn = currentBalance.subtract(totalInvested);

        // Deklarasi persentase keuntungan
        BigDecimal performancePct = BigDecimal.ZERO;
        // Jika total investasi lebih dari 0
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            // Hitung persentase keuntungan
            performancePct = totalReturn.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        // Build return
        return PortfolioSummaryResponse.builder()
                .currentBalance(currentBalance)
                .totalInvested(totalInvested)
                .totalReturn(totalReturn)
                .performancePercentage(performancePct)
                .build();
    }
}
