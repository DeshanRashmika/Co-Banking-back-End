package edu.icet.banking.notifications.api;

import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.security.BankingUserDetails;
import edu.icet.banking.notifications.api.dto.NotificationResponse;
import edu.icet.banking.notifications.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(Authentication authentication) {
        return notificationService.getNotifications(((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }

    @PutMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id, Authentication authentication) {
        return notificationService.markAsRead(id, ((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        notificationService.delete(id, ((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }
}

