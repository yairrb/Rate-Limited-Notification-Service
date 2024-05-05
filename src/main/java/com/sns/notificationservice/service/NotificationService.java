package com.sns.notificationservice.service;

import com.sns.notificationservice.dto.NotificationDTO;

public interface NotificationService {

    void sendNotification(NotificationDTO notificationDTO);
}
