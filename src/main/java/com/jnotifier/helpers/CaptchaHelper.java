package com.jnotifier.helpers;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaHelper {
    private static final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    private String generateRandomText() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();

        while (sb.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    public void generateCaptcha(String id) {
        captchaStore.put(id, generateRandomText());
    }

    public void generateCaptcha(String id, String captchaCode) {
        captchaStore.put(id, captchaCode);
    }

    public boolean validateCaptcha(String id, String captcha) {
        String storedCaptcha = captchaStore.get(id);

        if (storedCaptcha == null) {
            return false;
        }

        return storedCaptcha.equals(captcha);
    }

    public void clearCaptcha(String id) {
        captchaStore.remove(id);
    }
}
