package com.sns.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratelimit")
@Data
@Setter
@Getter
@Builder
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "topic", unique = true)
    private String topic;
    @Column(name = "limitrate")
    private Integer limit;
    @Column(name = "time")
    private Long timeInMilliseconds;

}
