package com.jnotifier.services.impl;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.RefreshToken;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.OtpRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.JwtResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.security.jwt.JwtUtils;
import com.jnotifier.services.IVerifyService;
import com.jnotifier.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VerifyService implements IVerifyService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Value("${jnotifier.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Override
    public ServiceReply login(String username) {
        ServiceReply serviceReply = new ServiceReply();
        Map<String, Object> map = new HashMap<>();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_USER", "Invalid credentials")));

        if (!user.getIsEmailVerified())
            throw new GenericException(ApiResponse.error("ACCOUNT_NOT_VERIFIED", "Please verify your account"));

        String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.findByUserId(user.getId())
                .orElseGet(() -> refreshTokenService.createRefreshToken(user.getId()));

        List<String> roles = List.of(user.getRole().getName().name());

        JwtResponse jwtResponse = new JwtResponse(jwt,
                refreshToken.getToken(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL + "/auth")
                .sameSite("None")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", jwtResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL)
                .sameSite("None")
                .build();

        map.put("jwtResponse", jwtResponse);
        map.put("refCookie", cookie);
        map.put("accessCookie", accessCookie);

        serviceReply.setHttpStatusCode(HttpStatusCode.valueOf(200));
        serviceReply.setReply(map);

        return serviceReply;
    }

    @Override
    public ServiceReply verifyEmail(String username) {
        ServiceReply serviceReply = new ServiceReply();
        Map<String, Object> map = new HashMap<>();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setIsEmailVerified(true);
        userRepository.save(user);

        map.put("message", "Email verified");

        serviceReply.setHttpStatusCode(HttpStatusCode.valueOf(200));
        serviceReply.setReply(map);
        return serviceReply;
    }

    @Override
    public ServiceReply forgotPassword(String username) {
        ServiceReply serviceReply = new ServiceReply();
        Map<String, Object> map = new HashMap<>();

        map.put("message", "Email verified");

        serviceReply.setHttpStatusCode(HttpStatusCode.valueOf(200));
        serviceReply.setReply(map);
        return serviceReply;
    }
}
