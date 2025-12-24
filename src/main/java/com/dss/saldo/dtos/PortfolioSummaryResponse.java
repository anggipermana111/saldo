package com.dss.saldo.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PortfolioSummaryResponse {
    private BigDecimal currentBalance;
    private BigDecimal totalInvested;
    private BigDecimal totalReturn;
    private BigDecimal performancePercentage;
}
