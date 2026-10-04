package com.jnotifier.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("welcomeNotification")
    private ChannelTopic welcomeNotificationChannel;

    @Autowired
    @Qualifier("authNotification")
    private ChannelTopic authNotificationChannel;

    @Autowired
    @Qualifier("otpNotification")
    private ChannelTopic otpNotificationChannel;

    public void publishWelcomeNotification(Object message) throws JsonProcessingException {
        logger.debug("Publishing Welcome Notification");

        String cleanJsonPayload = objectMapper.writeValueAsString(message);
        stringRedisTemplate.convertAndSend(welcomeNotificationChannel.getTopic(), cleanJsonPayload);
        logger.debug("Published Welcome Notification");
    }

    public void publishOTPNotification(Object message) throws JsonProcessingException {
        logger.debug("Publishing OTP Notification");

        String cleanJsonPayload = objectMapper.writeValueAsString(message);
        stringRedisTemplate.convertAndSend(otpNotificationChannel.getTopic(), cleanJsonPayload);
        logger.debug("Published OTP Notification");
    }

    public void publishAuthNotification(Object message) throws JsonProcessingException {
        logger.debug("Publishing Auth Notification");

        String cleanJsonPayload = objectMapper.writeValueAsString(message);
        stringRedisTemplate.convertAndSend(authNotificationChannel.getTopic(), cleanJsonPayload);
        logger.debug("Published Auth Notification");
    }
}
