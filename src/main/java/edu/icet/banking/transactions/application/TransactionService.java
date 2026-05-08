package edu.icet.banking.transactions.application;

import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.common.exception.InsufficientFundsException;
import edu.icet.banking.common.exception.InvalidOperationException;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import edu.icet.banking.notifications.application.NotificationService;
import edu.icet.banking.notifications.domain.entity.NotificationType;
import edu.icet.banking.transactions.api.dto.TransactionResponse;
import edu.icet.banking.transactions.api.dto.TransferRequest;
import edu.icet.banking.transactions.domain.entity.BankTransaction;
import edu.icet.banking.transactions.domain.entity.TransactionStatus;
import edu.icet.banking.transactions.domain.entity.TransactionType;
import edu.icet.banking.transactions.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    public List<TransactionResponse> getTransactions(Long accountId, String email) {
        accountRepository.findByIdAndUser_Email(accountId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        return transactionRepository.findAllByFromAccount_IdOrToAccount_IdOrderByCreatedAtDesc(accountId, accountId)
                .stream().map(TransactionResponse::from).toList();
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String email) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new InvalidOperationException("Transfer source and destination accounts must be different");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Transfer amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findByIdForUpdate(request.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getFromAccountId()));
        if (!fromAccount.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new InvalidOperationException("You cannot transfer from an account you do not own");
        }

        Account toAccount = accountRepository.findByIdForUpdate(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getToAccountId()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(String.valueOf(fromAccount.getId()), request.getAmount().toPlainString());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        BankTransaction transaction = transactionRepository.save(BankTransaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .build());

        notificationService.createNotification(
                fromAccount.getUser(),
                "Transfer completed",
                "Transferred " + request.getAmount() + " to account " + toAccount.getAccountNumber(),
                NotificationType.TRANSACTION);

        notificationService.createNotification(
                toAccount.getUser(),
                "Incoming transfer",
                "Received " + request.getAmount() + " from account " + fromAccount.getAccountNumber(),
                NotificationType.TRANSACTION);

        return TransactionResponse.from(transaction);
    }
}

