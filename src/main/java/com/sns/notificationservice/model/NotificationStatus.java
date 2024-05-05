package com.sns.notificationservice.model;

import lombok.Getter;

@Getter
public enum NotificationStatus {

    SENT("SENT", "Notification has been sent"),
    PENDING("PENDING", "Notification is waiting to be sent");


    NotificationStatus(String name, String description) {
    }
}
