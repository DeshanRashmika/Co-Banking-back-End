package edu.icet.banking.bills.api.dto;

import edu.icet.banking.bills.domain.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Long id;
    private Long accountId;
    private String payeeName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private String description;

    public static BillResponse from(Bill bill) {
        return BillResponse.builder()
                .id(bill.getId())
                .accountId(bill.getAccount().getId())
                .payeeName(bill.getPayeeName())
                .amount(bill.getAmount())
                .dueDate(bill.getDueDate())
                .status(bill.getStatus().name())
                .description(bill.getDescription())
                .build();
    }
}

