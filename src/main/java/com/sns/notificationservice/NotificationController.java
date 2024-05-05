package com.sns.notificationservice;


import com.sns.notificationservice.dto.NotificationDTO;
import com.sns.notificationservice.model.LimitReachedException;
import com.sns.notificationservice.service.RateLimitedNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final RateLimitedNotificationService rateLimitedService;

    public NotificationController(RateLimitedNotificationService rateLimitedService) {
        this.rateLimitedService = rateLimitedService;
    }

    @PostMapping( path = "/push", produces = "application/json")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationDTO notification) {

        try {
            this.rateLimitedService.sendNotification(notification);
            return ResponseEntity.ok().build();

        } catch (LimitReachedException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

    }

}
