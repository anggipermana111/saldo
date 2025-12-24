package com.dss.saldo.controllers;

import com.dss.saldo.dtos.PortfolioSummaryResponse;
import com.dss.saldo.services.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/summary")
    public ResponseEntity<PortfolioSummaryResponse> getPortfolioSummary() {
        PortfolioSummaryResponse response = portfolioService.calculatePortfolio();
        return ResponseEntity.ok(response);
    }
}
