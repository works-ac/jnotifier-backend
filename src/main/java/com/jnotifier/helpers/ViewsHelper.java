package com.jnotifier.helpers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ViewsHelper {
    @Autowired
    private NetworkHelper networkHelper;

    public Map<String, String> getClientDetails(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        String ipAddress = networkHelper.extractClientIp(request);
        String visitedPage = request.getHeader("X-User-Loc");
        String browserName = Optional.ofNullable(request.getHeader("X-Brow-Name")).orElse("N/A");
        String browserVersion = Optional.ofNullable(request.getHeader("X-Brow-Version")).orElse("N/A");
        String osName = Optional.ofNullable(request.getHeader("X-OS-Name")).orElse("N/A");
        String deviceType = Optional.ofNullable(request.getHeader("X-Device-Type")).orElse("N/A");
        String deviceVendor = Optional.ofNullable(request.getHeader("X-Device-Vendor")).orElse("N/A");


        map.put("ipAddress", ipAddress);
        map.put("visitedPage", visitedPage);
        map.put("browserName", browserName);
        map.put("browserVersion", browserVersion);
        map.put("osName", osName);
        map.put("deviceType", deviceType);
        map.put("deviceVendor", deviceVendor);
        return map;
    }
}
