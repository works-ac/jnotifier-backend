package com.jnotifier.filter;

import com.jnotifier.helpers.NetworkHelper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestLimitingFilter extends OncePerRequestFilter {
    @Autowired
    private NetworkHelper networkHelper;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(20)
                .refillGreedy(20, Duration.ofSeconds(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = networkHelper.extractClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Fix: Use getNanosToWaitForRefill() instead of getNanosToWait()
            long nanosToWait = probe.getNanosToWaitForRefill();
            long secondsToWait = (long) Math.ceil(nanosToWait / 1_000_000_000.0);

            response.setHeader("Retry-After", String.valueOf(secondsToWait));

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("text/plain");
            response.getWriter().write("429 Too Many Requests.");
        }
    }
}