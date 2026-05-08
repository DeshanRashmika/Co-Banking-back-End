package edu.icet.banking.investments.api;

import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.investments.api.dto.InvestmentBuyRequest;
import edu.icet.banking.investments.api.dto.InvestmentResponse;
import edu.icet.banking.investments.api.dto.InvestmentSellRequest;
import edu.icet.banking.investments.application.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping("/portfolio")
    public List<InvestmentResponse> portfolio(Authentication authentication) {
        return investmentService.getPortfolio(((User) authentication.getPrincipal()).getEmail());
    }

    @PostMapping("/buy")
    public InvestmentResponse buy(@Valid @RequestBody InvestmentBuyRequest request, Authentication authentication) {
        return investmentService.buy(request, ((User) authentication.getPrincipal()).getEmail());
    }

    @PostMapping("/sell/{id}")
    public InvestmentResponse sell(@PathVariable Long id, @Valid @RequestBody InvestmentSellRequest request, Authentication authentication) {
        return investmentService.sell(id, request, ((User) authentication.getPrincipal()).getEmail());
    }
}

