package com.jnotifier.helpers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class NetworkHelper {
    /**
     * Resolves the real client IP address, respecting common reverse-proxy headers.
     *
     * <p>Resolution order:
     * <ol>
     *   <li>{@code X-Forwarded-For} — may contain a comma-separated chain of IPs
     *       (e.g. {@code "client, proxy1, proxy2"}); we take the <em>leftmost</em>
     *       (original client) entry.</li>
     *   <li>{@code X-Real-IP} — single IP set by Nginx when only one proxy hop
     *       exists and {@code proxy_set_header X-Real-IP $remote_addr} is configured.</li>
     *   <li>{@link HttpServletRequest#getRemoteAddr()} — direct TCP peer address,
     *       used when no proxy headers are present.</li>
     * </ol>
     */
    public String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // X-Forwarded-For: client, proxy1, proxy2 — take the first (leftmost) IP
            return xff.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}
