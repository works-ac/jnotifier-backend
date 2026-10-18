package com.jnotifier.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.services.impl.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailHelper {
    @Autowired
    private RedisService redisService;

    public void sendEmailOTP(Map<String, String> content) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();

        payload.put("content", content);
        payload.put("timestamp", System.currentTimeMillis());
        redisService.publishOTPNotification(payload);
    }

    public void sendWelcomeNotification(Map<String, String> content) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();

        payload.put("content", content);
        payload.put("timestamp", System.currentTimeMillis());
        redisService.publishWelcomeNotification(payload);
    }

    public void sendAuthNotification(Map<String, String> content) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();

        payload.put("content", content);
        payload.put("timestamp", System.currentTimeMillis());
        redisService.publishAuthNotification(payload);
    }
}
