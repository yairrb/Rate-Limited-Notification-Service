package com.sns.notificationservice;

import com.sns.notificationservice.dto.NotificationDTO;
import com.sns.notificationservice.model.LimitReachedException;
import com.sns.notificationservice.model.Notification;
import com.sns.notificationservice.model.NotificationStatus;
import com.sns.notificationservice.model.RateLimit;
import com.sns.notificationservice.repository.NotificationRepository;
import com.sns.notificationservice.repository.RateLimitRepository;
import com.sns.notificationservice.service.RateLimitedNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RateLimitedNotificationServiceTest {

    @Mock
    private RateLimitRepository rateLimitRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    private RateLimitedNotificationService service;

    private NotificationDTO notificationDTO;
    private Notification notification;
    private RateLimit rateLimit;

    @BeforeEach
    public void setUp() {
        service = new RateLimitedNotificationService(rateLimitRepository, notificationRepository, mailSender);

        notificationDTO = new NotificationDTO("Test Topic", "Test Message", "recipient@email.com");
        notification = mapNotification(notificationDTO);

        rateLimit = RateLimit.builder().topic("Test Topic")
                .limit(1)
                .timeInMilliseconds(1000L).build();
    }

    @Test
    public void testSendNotificationNoRateLimitSuccess() throws LimitReachedException {
        NotificationDTO notificationDTO = new NotificationDTO("Test Topic", "Test Message", "recipient@example.com");

        when(rateLimitRepository.findRateLimitByTopic(anyString())).thenReturn(null);

        service.sendNotification(notificationDTO);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendNotificationWithinRateLimitSuccess() throws LimitReachedException {
        when(rateLimitRepository.findRateLimitByTopic(notification.getTopic())).thenReturn(rateLimit);
        when(notificationRepository.findByTopicAndRecipientAndTimestampAfter(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        service.sendNotification(notificationDTO);

//        verify(notificationRepository).save(notification);
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendNotificationRateLimitExceededThrowsException() throws LimitReachedException {
        when(rateLimitRepository.findRateLimitByTopic(notification.getTopic())).thenReturn(rateLimit);

        List<Notification> pastNotifications = Collections.singletonList(notification);
        when(notificationRepository.findByTopicAndRecipientAndTimestampAfter(any(), any(), any()))
                .thenReturn(pastNotifications);

        assertThrows(LimitReachedException.class, () -> service.sendNotification(notificationDTO));

        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }


    private Notification mapNotification(NotificationDTO dto) {

        return Notification.builder().topic(dto.getTopic()).message(dto.getMessage()).recipient(dto.getRecipient()).timestamp(LocalDateTime.now()).status(NotificationStatus.PENDING).build();
    }
}
