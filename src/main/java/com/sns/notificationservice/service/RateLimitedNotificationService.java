package com.sns.notificationservice.service;

import com.sns.notificationservice.dto.NotificationDTO;
import com.sns.notificationservice.model.LimitReachedException;
import com.sns.notificationservice.model.Notification;
import com.sns.notificationservice.model.NotificationStatus;
import com.sns.notificationservice.model.RateLimit;
import com.sns.notificationservice.repository.NotificationRepository;
import com.sns.notificationservice.repository.RateLimitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RateLimitedNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitedNotificationService.class);

    private final RateLimitRepository rateLimitRepository;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public RateLimitedNotificationService(RateLimitRepository repository, NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.rateLimitRepository = repository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        sendNotification(mapNotification(notification));
    }


    public void sendNotification(Notification notification) {
        //check if limit exist
        RateLimit ratelimit = this.rateLimitRepository.findRateLimitByTopic(notification.getTopic());

        if (ratelimit != null) {

            //check the rate limit
            LocalDateTime startTime = LocalDateTime.now().minusNanos(ratelimit.getTimeInMilliseconds() * 1000000);

            List<Notification> notifications = this.notificationRepository.findByTopicAndRecipientAndTimestampAfter(ratelimit.getTopic(), notification.getRecipient()
                    , startTime);

            if (notifications.size() < ratelimit.getLimit()) {
                notification.setStatus(NotificationStatus.SENT);
                send(notification);
            } else {

                logger.warn(String.format("Rate limited reached... Saving notification with Topic=%s and Recipient=%s", notification.getTopic()
                        , notification.getRecipient()));
                notification.setStatus(NotificationStatus.PENDING);
                throw new LimitReachedException("Limit rate reached. Notification will be saved for later.");

            }

        } else {

            logger.warn(String.format("About to send a notification without a predefined topic. topic = %s recipient=%s", notification.getTopic()
                    , notification.getRecipient()));
            notification.setStatus(NotificationStatus.SENT);
            send(notification);
        }

        //save the notification for later
        this.notificationRepository.save(notification);

    }

    private void send(Notification notification) {

        logger.info(String.format("about to send email notification to %s", notification.getRecipient()));
        //send
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yair.ruizbarbas@gmail.com");
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getTopic());
        message.setText(notification.getMessage());

        this.mailSender.send(message);
    }

    private Notification mapNotification(NotificationDTO dto) {

        return Notification.builder().topic(dto.getTopic()).message(dto.getMessage()).recipient(dto.getRecipient()).timestamp(LocalDateTime.now()).status(NotificationStatus.PENDING).build();
    }

}
