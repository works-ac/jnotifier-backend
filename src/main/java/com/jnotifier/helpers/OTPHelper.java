package com.jnotifier.helpers;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPHelper {
    private static final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void generateOTP(String id, String captchaCode) {
        otpStore.put(id, captchaCode);
    }

    public boolean validateOTP(String id, String captcha) {
        String storedOTP = otpStore.get(id);

        if (storedOTP == null) {
            return false;
        }

        return storedOTP.equals(captcha);
    }

    public void removeOTP(String id) {
        otpStore.remove(id);
    }
}
