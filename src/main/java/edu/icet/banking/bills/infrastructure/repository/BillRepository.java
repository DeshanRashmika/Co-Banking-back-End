package edu.icet.banking.bills.infrastructure.repository;

import edu.icet.banking.bills.domain.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findAllByUser_EmailOrderByDueDateAsc(String email);
    Optional<Bill> findByIdAndUser_Email(Long id, String email);
}

