package edu.icet.banking.investments.api.dto;

import edu.icet.banking.investments.domain.entity.Investment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentResponse {
    private Long id;
    private Long accountId;
    private String symbol;
    private BigDecimal shares;
    private BigDecimal purchasePrice;
    private BigDecimal totalValue;
    private String status;
    private LocalDateTime purchasedAt;

    public static InvestmentResponse from(Investment investment) {
        return InvestmentResponse.builder()
                .id(investment.getId())
                .accountId(investment.getAccount().getId())
                .symbol(investment.getSymbol())
                .shares(investment.getShares())
                .purchasePrice(investment.getPurchasePrice())
                .totalValue(investment.getTotalValue())
                .status(investment.getStatus().name())
                .purchasedAt(investment.getPurchasedAt())
                .build();
    }
}

