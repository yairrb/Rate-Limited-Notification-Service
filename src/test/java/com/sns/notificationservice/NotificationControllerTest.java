package com.sns.notificationservice;

import com.sns.notificationservice.dto.NotificationDTO;
import com.sns.notificationservice.model.LimitReachedException;
import com.sns.notificationservice.service.RateLimitedNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationControllerTest {

    @Mock
    private RateLimitedNotificationService rateLimitedService;

    private NotificationController controller;
    private NotificationDTO notificationDTO;

    @BeforeEach
    public void setUp() {
        controller = new NotificationController(rateLimitedService);
        notificationDTO = new NotificationDTO("Test Topic", "Test Message", "recipient@email.com");
    }

    @Test
    public void testSendNotification_Success() throws LimitReachedException {
        controller.sendNotification(notificationDTO);

        verify(rateLimitedService).sendNotification(notificationDTO);
        verify(rateLimitedService, never()).throwException(LimitReachedException.class); // Shouldn't throw exception
        assertEquals(ResponseEntity.ok().build(), controller.sendNotification(notificationDTO));
    }

    @Test
    public void testSendNotification_RateLimitExceeded() throws LimitReachedException {
        doThrow(new LimitReachedException("Limit reached")).when(rateLimitedService).sendNotification(notificationDTO);

        assertEquals(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build(), controller.sendNotification(notificationDTO));
        verify(rateLimitedService).sendNotification(notificationDTO);
        assertThrows(LimitReachedException.class, () -> rateLimitedService.sendNotification(notificationDTO));
    }
}
