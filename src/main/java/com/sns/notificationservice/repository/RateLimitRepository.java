package com.sns.notificationservice.repository;

import com.sns.notificationservice.model.RateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, Long> {

    RateLimit findRateLimitByTopic(String topic);


}
