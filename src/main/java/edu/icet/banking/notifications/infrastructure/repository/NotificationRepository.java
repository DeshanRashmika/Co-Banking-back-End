package edu.icet.banking.notifications.infrastructure.repository;

import edu.icet.banking.notifications.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUser_EmailOrderByCreatedAtDesc(String email);

    Optional<Notification> findByIdAndUser_Email(Long id, String email);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}

