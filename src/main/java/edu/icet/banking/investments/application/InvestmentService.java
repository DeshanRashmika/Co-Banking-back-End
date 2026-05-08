package edu.icet.banking.investments.application;

import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.common.exception.InsufficientFundsException;
import edu.icet.banking.common.exception.InvalidOperationException;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import edu.icet.banking.investments.api.dto.InvestmentBuyRequest;
import edu.icet.banking.investments.api.dto.InvestmentResponse;
import edu.icet.banking.investments.api.dto.InvestmentSellRequest;
import edu.icet.banking.investments.domain.entity.Investment;
import edu.icet.banking.investments.domain.entity.InvestmentStatus;
import edu.icet.banking.investments.infrastructure.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final AccountRepository accountRepository;

    public List<InvestmentResponse> getPortfolio(String email) {
        return investmentRepository.findAllByUser_EmailOrderByPurchasedAtDesc(email).stream()
                .map(InvestmentResponse::from).toList();
    }

    @Transactional
    public InvestmentResponse buy(InvestmentBuyRequest request, String email) {
        if (request.getShares().compareTo(BigDecimal.ZERO) <= 0 || request.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Shares and purchase price must be greater than zero");
        }

        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
        if (!account.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new InvalidOperationException("You cannot buy investments from an account you do not own");
        }

        BigDecimal totalCost = request.getShares().multiply(request.getPurchasePrice());
        if (account.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientFundsException(String.valueOf(account.getId()), totalCost.toPlainString());
        }

        account.setBalance(account.getBalance().subtract(totalCost));
        accountRepository.save(account);

        Investment investment = investmentRepository.save(Investment.builder()
                .user(account.getUser())
                .account(account)
                .symbol(request.getSymbol().toUpperCase())
                .shares(request.getShares())
                .purchasePrice(request.getPurchasePrice())
                .totalValue(totalCost)
                .status(InvestmentStatus.ACTIVE)
                .build());

        return InvestmentResponse.from(investment);
    }

    @Transactional
    public InvestmentResponse sell(Long id, InvestmentSellRequest request, String email) {
        if (request.getShares().compareTo(BigDecimal.ZERO) <= 0 || request.getSellPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Shares and sell price must be greater than zero");
        }

        Investment investment = investmentRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Investment", "id", id));
        if (investment.getStatus() != InvestmentStatus.ACTIVE) {
            throw new InvalidOperationException("Investment is not active");
        }
        if (investment.getShares().compareTo(request.getShares()) < 0) {
            throw new InvalidOperationException("Cannot sell more shares than owned");
        }

        Long accountId = investment.getAccount().getId();
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        BigDecimal proceeds = request.getShares().multiply(request.getSellPrice());

        account.setBalance(account.getBalance().add(proceeds));
        accountRepository.save(account);

        investment.setShares(investment.getShares().subtract(request.getShares()));
        investment.setTotalValue(investment.getShares().multiply(investment.getPurchasePrice()));
        if (investment.getShares().compareTo(BigDecimal.ZERO) == 0) {
            investment.setStatus(InvestmentStatus.SOLD);
        }
        investment = investmentRepository.save(investment);
        return InvestmentResponse.from(investment);
    }
}
