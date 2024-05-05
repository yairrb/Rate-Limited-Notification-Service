package com.sns.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification-pool")
@Getter
@Setter
@Builder
public class Notification {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "body")
    private String message;
    @Column(name = "recipient")
    private String recipient;

    @Column(name = "topic")
    private String topic;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "sentTime")
    private LocalDateTime timestamp;


}

