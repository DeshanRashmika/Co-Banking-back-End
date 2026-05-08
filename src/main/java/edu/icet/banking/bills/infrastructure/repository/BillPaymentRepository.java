package edu.icet.banking.bills.infrastructure.repository;

import edu.icet.banking.bills.domain.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    List<BillPayment> findAllByBill_IdOrderByPaymentDateDesc(Long billId);
}

