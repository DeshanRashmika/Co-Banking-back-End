package edu.icet.banking.bills.api.dto;

import edu.icet.banking.bills.domain.entity.BillPayment;
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
public class BillPaymentResponse {
    private Long id;
    private Long billId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String referenceNumber;

    public static BillPaymentResponse from(BillPayment payment) {
        return BillPaymentResponse.builder()
                .id(payment.getId())
                .billId(payment.getBill().getId())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .referenceNumber(payment.getReferenceNumber())
                .build();
    }
}

