package edu.icet.banking.bills.application;

import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.bills.api.dto.BillPaymentRequest;
import edu.icet.banking.bills.api.dto.BillPaymentResponse;
import edu.icet.banking.bills.api.dto.BillResponse;
import edu.icet.banking.bills.domain.entity.Bill;
import edu.icet.banking.bills.domain.entity.BillPayment;
import edu.icet.banking.bills.domain.entity.BillStatus;
import edu.icet.banking.bills.infrastructure.repository.BillPaymentRepository;
import edu.icet.banking.bills.infrastructure.repository.BillRepository;
import edu.icet.banking.common.exception.InsufficientFundsException;
import edu.icet.banking.common.exception.InvalidOperationException;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import edu.icet.banking.notifications.application.NotificationService;
import edu.icet.banking.notifications.domain.entity.NotificationType;
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
public class BillService {

    private final BillRepository billRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public List<BillResponse> getBills(String email) {
        return billRepository.findAllByUser_EmailOrderByDueDateAsc(email).stream()
                .map(BillResponse::from).toList();
    }

    @Transactional
    public BillPaymentResponse payBill(BillPaymentRequest request, String email) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Payment amount must be greater than zero");
        }

        Bill bill = billRepository.findByIdAndUser_Email(request.getBillId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", request.getBillId()));
        if (bill.getStatus() == BillStatus.PAID) {
            throw new InvalidOperationException("Bill is already paid");
        }
        if (request.getAmount().compareTo(bill.getAmount()) < 0) {
            throw new InvalidOperationException("Payment amount must cover the bill amount");
        }

        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
        if (!account.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new InvalidOperationException("You cannot pay bills from an account you do not own");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(String.valueOf(account.getId()), request.getAmount().toPlainString());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        BillPayment payment = billPaymentRepository.save(BillPayment.builder()
                .bill(bill)
                .amountPaid(request.getAmount())
                .paymentMethod("ACCOUNT_TRANSFER")
                .referenceNumber("BILL-" + bill.getId() + "-" + System.currentTimeMillis())
                .build());

        transactionRepository.save(BankTransaction.builder()
                .fromAccount(account)
                .toAccount(account)
                .amount(request.getAmount())
                .transactionType(TransactionType.BILL_PAYMENT)
                .description("Bill payment for " + bill.getPayeeName())
                .status(TransactionStatus.COMPLETED)
                .build());

        notificationService.createNotification(
                bill.getUser(),
                "Bill paid",
                "Your bill to " + bill.getPayeeName() + " has been paid",
                NotificationType.BILL);

        return BillPaymentResponse.from(payment);
    }
}
