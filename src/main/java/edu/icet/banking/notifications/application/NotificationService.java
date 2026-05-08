package edu.icet.banking.notifications.application;

import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import edu.icet.banking.notifications.api.dto.NotificationResponse;
import edu.icet.banking.notifications.domain.entity.Notification;
import edu.icet.banking.notifications.domain.entity.NotificationType;
import edu.icet.banking.notifications.infrastructure.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(String email) {
        return notificationRepository.findAllByUser_EmailOrderByCreatedAtDesc(email).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long id, String email) {
        Notification notification = notificationRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public void delete(Long id, String email) {
        Notification notification = notificationRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notificationRepository.delete(notification);
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, NotificationType type) {
        return notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .isRead(false)
                .build());
    }
}

