package edu.icet.banking.bills.api;

import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.security.BankingUserDetails;
import edu.icet.banking.bills.api.dto.BillPaymentRequest;
import edu.icet.banking.bills.api.dto.BillPaymentResponse;
import edu.icet.banking.bills.api.dto.BillResponse;
import edu.icet.banking.bills.application.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping
    public List<BillResponse> getBills(Authentication authentication) {
        return billService.getBills(((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }

    @PostMapping("/pay")
    public BillPaymentResponse pay(@Valid @RequestBody BillPaymentRequest request, Authentication authentication) {
        return billService.payBill(request, ((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }
}

