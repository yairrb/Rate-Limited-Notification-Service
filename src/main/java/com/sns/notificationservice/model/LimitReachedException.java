package com.sns.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LimitReachedException extends RuntimeException{


    public LimitReachedException(String s) {
        super(s);
    }
}
