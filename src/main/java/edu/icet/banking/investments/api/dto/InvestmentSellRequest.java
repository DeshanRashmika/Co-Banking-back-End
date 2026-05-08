package edu.icet.banking.investments.api.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class InvestmentSellRequest {
    @NotNull(message = "Shares is required")
    @DecimalMin(value = "0.0001", message = "Shares must be greater than zero")
    private BigDecimal shares;

    @NotNull(message = "Sell price is required")
    @DecimalMin(value = "0.01", message = "Sell price must be greater than zero")
    private BigDecimal sellPrice;
}

