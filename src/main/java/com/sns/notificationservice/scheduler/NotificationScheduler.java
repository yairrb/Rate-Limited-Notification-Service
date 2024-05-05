package com.sns.notificationservice.scheduler;

import com.sns.notificationservice.repository.NotificationRepository;
import com.sns.notificationservice.service.RateLimitedNotificationService;
import com.sns.notificationservice.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    private final NotificationRepository notificationRepository;
    private final RateLimitedNotificationService notificationService;

    public NotificationScheduler(NotificationRepository notificationRepository, RateLimitedNotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }


    @Scheduled(fixedRate = 120000) //every two minutes. It's an issue because it's always running. SQS and SNS it's better solution for this.
    public void sendPendingNotifications() {

        //pick up pendings
        this.notificationRepository.findNotificationsByStatusOrderByTimestampAsc(NotificationStatus.PENDING)
                .forEach(pending -> {
                    try {
                        this.notificationService.sendNotification(pending);
                    }catch (Exception e){
                        logger.info("Some notification are still pending. Check in case of starvation");
                    }
                });
    }



}
