package com.sns.notificationservice.repository;

import com.sns.notificationservice.model.NotificationStatus;
import com.sns.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTopicAndRecipientAndTimestampAfter(String topic, String recipient, LocalDateTime date);

    List<Notification> findNotificationsByStatusOrderByTimestampAsc(NotificationStatus status);

}
