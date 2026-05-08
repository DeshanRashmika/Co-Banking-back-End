package edu.icet.banking.investments.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentBuyRequest {
    @NotNull(message = "Account id is required")
    private Long accountId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Shares is required")
    @DecimalMin(value = "0.0001", message = "Shares must be greater than zero")
    private BigDecimal shares;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.01", message = "Purchase price must be greater than zero")
    private BigDecimal purchasePrice;
}

