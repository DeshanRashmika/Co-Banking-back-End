package edu.icet.banking.bills.application;

import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.domain.entity.AccountStatus;
import edu.icet.banking.accounts.domain.entity.AccountType;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import edu.icet.banking.bills.api.dto.BillPaymentRequest;
import edu.icet.banking.bills.domain.entity.Bill;
import edu.icet.banking.bills.domain.entity.BillStatus;
import edu.icet.banking.bills.infrastructure.repository.BillRepository;
import edu.icet.banking.common.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BillServiceIT {

    @Autowired private BillService billService;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private BillRepository billRepository;

    @Test
    void payBill_shouldMarkBillPaidAndDebitAccount() {
        User user = userRepository.save(User.builder()
                .email("carol@example.com")
                .firstName("Carol")
                .lastName("Doe")
                .passwordHash("x")
                .build());
        Account account = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("C-100")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("500.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());
        Bill bill = billRepository.save(Bill.builder()
                .user(user)
                .account(account)
                .payeeName("Internet Provider")
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.now().plusDays(5))
                .status(BillStatus.PENDING)
                .description("Monthly internet")
                .build());

        billService.payBill(BillPaymentRequest.builder()
                .billId(bill.getId())
                .accountId(account.getId())
                .amount(new BigDecimal("100.00"))
                .build(), user.getEmail());

        assertEquals(0, new BigDecimal("400.00").compareTo(accountRepository.findById(account.getId()).orElseThrow().getBalance()));
        assertEquals(BillStatus.PAID, billRepository.findById(bill.getId()).orElseThrow().getStatus());
    }

    @Test
    void payBill_shouldRejectInsufficientFunds() {
        User user = userRepository.save(User.builder()
                .email("dave@example.com")
                .firstName("Dave")
                .lastName("Doe")
                .passwordHash("x")
                .build());
        Account account = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("D-100")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("50.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());
        Bill bill = billRepository.save(Bill.builder()
                .user(user)
                .account(account)
                .payeeName("Gym")
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.now().plusDays(5))
                .status(BillStatus.PENDING)
                .build());

        assertThrows(InsufficientFundsException.class, () -> billService.payBill(BillPaymentRequest.builder()
                .billId(bill.getId())
                .accountId(account.getId())
                .amount(new BigDecimal("100.00"))
                .build(), user.getEmail()));

        assertEquals(0, new BigDecimal("50.00").compareTo(accountRepository.findById(account.getId()).orElseThrow().getBalance()));
        assertEquals(BillStatus.PENDING, billRepository.findById(bill.getId()).orElseThrow().getStatus());
    }
}

