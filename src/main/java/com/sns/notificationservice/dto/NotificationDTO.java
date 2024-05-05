package com.sns.notificationservice.dto;

import lombok.*;

@Setter
@Getter
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    String topic;
    String recipient;
    String message;
}
