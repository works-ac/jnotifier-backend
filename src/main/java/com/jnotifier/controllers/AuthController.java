package com.jnotifier.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.app.JNotifierEnums;
import com.jnotifier.helpers.CaptchaHelper;
import com.jnotifier.helpers.EmailHelper;
import com.jnotifier.helpers.OTPHelper;
import com.jnotifier.payload.pojo.SimpleUserPojo;
import com.jnotifier.payload.request.*;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.RedisService;
import com.jnotifier.services.impl.VerifyService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.entity.User;
import com.jnotifier.entity.RefreshToken;
import com.jnotifier.payload.response.JwtResponse;
import com.jnotifier.payload.response.TokenRefreshResponse;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.security.jwt.JwtUtils;
import com.jnotifier.services.RefreshTokenService;
import com.jnotifier.exception.GenericException;
import com.jnotifier.exception.TokenRefreshException;


@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${jnotifier.app.default-otp-enabled}")
    private boolean defaultOtpEnabled;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    CaptchaHelper captchaHelper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private RedisService redisService;

    @Value("${notification.support.email}")
    private String notificationSupportEmail;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private EmailHelper emailHelper;

    @Autowired
    private OTPHelper otpHelper;

    private Map<String, String> getOtpPayload(User user, String otp) {
        Map<String, String> payload = new HashMap<>();

        payload.put("userName", user.getUsername());
        payload.put("name", user.getFullname());
        payload.put("subject", "Request for new OTP reg.");
        payload.put("email", user.getEmail());
        payload.put("supportEmail", notificationSupportEmail);
        payload.put("otp", otp);

        return payload;
    }

    @GetMapping("/captcha")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String captchaCode = generateRandomText();

        String captchaImageBase64 = generateCaptchaImage(captchaCode);

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captchaImage", captchaImageBase64);

        captchaHelper.generateCaptcha(captchaId, captchaCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, String>>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        //1. Validate Captcha
        String captchaId = loginRequest.getCaptchaId();
        String captcha = loginRequest.getCaptchaValue();

        if (!captchaHelper.validateCaptcha(captchaId, captcha)) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_CAPTCHA", "Captcha is incorrect or expired."));
        }
        captchaHelper.clearCaptcha(captchaId);

        // 2. Authenticate username and password credentials
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));

        // 3. Generate OTP
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }

        otpHelper.generateOTP(loginRequest.getUsername(), otpCode);

        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsername(), loginRequest.getUsername())
                .orElseThrow(() -> new GenericException(ApiResponse.error("USER_NOT_FOUND", "Invalid credentials.")));

        boolean isAccountSuspended = Optional.ofNullable(user.getIsSuspended()).orElse(false);
        boolean isAccountDeleted = Optional.ofNullable(user.getIsDeleted()).orElse(false);

        if (isAccountSuspended)
            throw new GenericException(ApiResponse.error("ACCOUNT_ERR", "Can't login, account is in suspended mode."));
        if (isAccountDeleted)
            throw new GenericException(ApiResponse.error("ACCOUNT_ERR", "Can't login, account is deleted"));

        Map<String, String> data = new HashMap<>();
        data.put("username", loginRequest.getUsername());
        data.put("email", user.getEmail());
        data.put("status", "OTP_REQUIRED");
        data.put("message", "OTP verification code has been generated. Please verify to complete sign-in.");

        if (!defaultOtpEnabled) {
            logger.info("[OTP Verification] Generated OTP for authentication purposes {} for user {}", otpCode, loginRequest.getUsername());

            Map<String, String> content = getOtpPayload(user, otpCode);
            emailHelper.sendEmailOTP(content);
        }

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Object>> resendOtp(@Valid @RequestBody ResendOTPRequest resendOTPRequest)
            throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        User user = userRepository.findByUsernameOrEmail(resendOTPRequest.getUsername(), resendOTPRequest.getUsername()).orElseThrow(() -> new GenericException(ApiResponse.error("USER_NOT_FOUND", "Username or Password is wrong.")));
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }

        otpHelper.generateOTP(resendOTPRequest.getUsername(), otpCode);
        data.put("message", "OTP sent successfully");

        if (!defaultOtpEnabled) {
            logger.info("[OTP Verification] Generated OTP {} for user {}", otpCode, resendOTPRequest.getUsername());
            Map<String, String> welcomeNotificationContent = getOtpPayload(user, otpCode);
            emailHelper.sendEmailOTP(welcomeNotificationContent);
        }

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest)
            throws JsonProcessingException {
        User user = userRepository.findByUsernameOrEmail(emailVerifyRequest.getEmail(), emailVerifyRequest.getEmail()).
                orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_CRED", "Invalid Credentials")));

        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }
        otpHelper.generateOTP(emailVerifyRequest.getEmail(), otpCode);

        if (!defaultOtpEnabled) {
            Map<String, String> content = getOtpPayload(user, otpCode);
            emailHelper.sendEmailOTP(content);
        }

        return ResponseEntity.ok(ApiResponse.success(new SimpleUserPojo(user.getUsername(), user.getEmail())));
    }

    @PostMapping("/forgot-pwd")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
        User user = userRepository.findByUsernameOrEmail(emailVerifyRequest.getEmail(), emailVerifyRequest.getEmail()).
                orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_CRED", "Invalid Credentials")));

        String oldHashedPwd = user.getPassword();
        String newHashedPwd = encoder.encode(emailVerifyRequest.getPassword());

        if (oldHashedPwd.equals(newHashedPwd))
            throw new GenericException(ApiResponse.error("INVALID_CRED", "Please set a unique password as this matches with your current password."));

        user.setPassword(newHashedPwd);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(new SimpleUserPojo(user.getUsername(), user.getEmail())));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
        String verificationType = otpRequest.getVerificationType();
        ServiceReply serviceReply;

        if (!otpHelper.validateOTP(otpRequest.getUsername(), otpRequest.getOtpCode())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_OTP", "OTP is incorrect or expired."));
        }
        otpHelper.removeOTP(otpRequest.getUsername());

        JNotifierEnums verificationTypeEnum = JNotifierEnums.fromString(verificationType);

        if (verificationTypeEnum == JNotifierEnums.LOGIN) {
            serviceReply = verifyService.login(otpRequest.getUsername());
            ResponseCookie refCookie = (ResponseCookie) serviceReply.getReply().get("refCookie");
            ResponseCookie accessCookie = (ResponseCookie) serviceReply.getReply().get("accessCookie");
            JwtResponse body = (JwtResponse) serviceReply.getReply().get("jwtResponse");

            return ResponseEntity.status(serviceReply.getHttpStatusCode())
                    .header(HttpHeaders.SET_COOKIE, refCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .body(ApiResponse.success(body));
        } else if (verificationTypeEnum == JNotifierEnums.EMAIL_VERIFY) {
            serviceReply = verifyService.verifyEmail(otpRequest.getUsername());
            Object response = serviceReply.getReply();

            return ResponseEntity.status(serviceReply.getHttpStatusCode()).body(ApiResponse.success(response));
        } else if (verificationTypeEnum == JNotifierEnums.FORGOT_PWD) {
            serviceReply = verifyService.forgotPassword(otpRequest.getUsername());
            java.lang.Object reply = serviceReply.getReply();
            return ResponseEntity.status(serviceReply.getHttpStatusCode()).body(ApiResponse.success(reply));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_VERIFICATION_TYPE", "Invalid verification type."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws JsonProcessingException {
        String captchaId = signUpRequest.getCaptchaId();
        String captcha = signUpRequest.getCaptcha();

        if (signUpRequest.getIsPwd() == null || signUpRequest.getCategory() == null || signUpRequest.getCategory().isEmpty()) {
            throw new GenericException(ApiResponse.error("INVALID_FORM_DATA", "Please fill all the required fields."));
        }

        if (!captchaHelper.validateCaptcha(captchaId, captcha)) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_CAPTCHA", "Captcha is incorrect or expired."));
        }
        captchaHelper.clearCaptcha(captchaId);

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "Error: Email is already in use!"));
        }

        String requestedRole = signUpRequest.getRole();
        if (requestedRole == null || requestedRole.trim().isEmpty()) {
            requestedRole = "user";
        }

        Role userRole;
        if (requestedRole.equalsIgnoreCase("admin")) {
            // Admin role registration is protected and can only be done by SUPERADMIN
            Authentication callerAuth = SecurityContextHolder.getContext().getAuthentication();

            if (!callerAuth.isAuthenticated() ||
                    callerAuth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("FORBIDDEN", "Error: Only SUPERADMIN accounts can register new ADMIN users."));
            }
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not initialized in database."));
        } else if (requestedRole.equalsIgnoreCase("superadmin")) {
            userRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: SUPERADMIN role not initialized in database."));
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: USER role not initialized in database."));
        }

        if (userRole.getName().name().equalsIgnoreCase(ERole.ROLE_SUPERADMIN.name()) ||
                userRole.getName().name().equalsIgnoreCase(ERole.ROLE_SYSADMIN.name()))
            throw new GenericException(ApiResponse.error("INVALID_ROLE", "Please enter a valid role"));

        if (userRole.getName().name().equalsIgnoreCase(ERole.ROLE_ADMIN.name()) && (signUpRequest.getCompanyName() == null || signUpRequest.getAddress() == null))
            throw new GenericException(ApiResponse.error("INVALID_USER", "Please fill all the required fields."));

        // Generate unique system-level username containing timestamp & name alphabets
        String cleanName = signUpRequest.getFullName().toLowerCase().replaceAll("[^a-zA-Z]", "");
        if (cleanName.isEmpty()) {
            cleanName = "user";
        }

        String generatedUsername = cleanName + "_" + System.currentTimeMillis();

        // Create new user's account
        User user = new User(
                signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getDob(),
                signUpRequest.getGender(),
                signUpRequest.getMobile(), signUpRequest.getCategory(), signUpRequest.getIsPwd(), false);

        user.setUsername(generatedUsername);
        user.setRole(userRole);
        userRepository.save(user);

        Map<String, String> reply = new HashMap<>();
        Map<String, Object> welcomeNotificationMsg = new HashMap<>();
        Map<String, String> welcomeNotificationContent = new HashMap<>();
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }
        otpHelper.generateOTP(generatedUsername, otpCode);

        //Creating content object for welcome notification
        welcomeNotificationContent.put("userName", user.getUsername());
        welcomeNotificationContent.put("name", user.getFullname());
        welcomeNotificationContent.put("subject", "Verification of newly created account reg.");
        welcomeNotificationContent.put("email", user.getEmail());
        welcomeNotificationContent.put("supportEmail", notificationSupportEmail);
        welcomeNotificationContent.put("gender", user.getGender());
        welcomeNotificationContent.put("category", user.getCategory());
        welcomeNotificationContent.put("dob", user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        welcomeNotificationContent.put("otp", otpCode);

        //Creating actual welcome notification payload.
        welcomeNotificationMsg.put("timestamp", System.currentTimeMillis());
        welcomeNotificationMsg.put("content", welcomeNotificationContent);

        reply.put("message", "User successfully registered!");
        reply.put("username", generatedUsername);

        redisService.publishWelcomeNotification(welcomeNotificationMsg);
        return ResponseEntity.ok(ApiResponse
                .success(reply));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshtoken(
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            @Valid @RequestBody(required = false) TokenRefreshRequest request) {

        String requestRefreshToken = cookieRefreshToken;
        if (requestRefreshToken == null || requestRefreshToken.trim().isEmpty()) {
            if (request != null) {
                requestRefreshToken = request.getRefreshToken();
            }
        }

        if (requestRefreshToken == null || requestRefreshToken.trim().isEmpty()) {
            throw new TokenRefreshException("", "Refresh token is missing from cookies and request body!");
        }

        String finalToken = requestRefreshToken;
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.findByToken(cookieRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new TokenRefreshResponse(token, finalToken);
                })
                .orElseThrow(() -> new TokenRefreshException(finalToken,
                        "Refresh token is not in database!"));

        ResponseCookie refCookie = ResponseCookie.from("refreshToken", finalToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(JNotifierConstants.API_BASE_URL + "/auth")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokenRefreshResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(JNotifierConstants.API_BASE_URL)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(ApiResponse.success(tokenRefreshResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refreshToken") String cookieRefreshToken,
                                                    @CookieValue(name = "accessToken") String cookieAccessToken) {
        RefreshToken token = refreshTokenService.findByToken(cookieRefreshToken).
                orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_TOKEN", "Invalid refresh token!")));

        User user = token.getUser();
        refreshTokenService.deleteByUserId(user.getId());

        ResponseCookie refCookie = ResponseCookie.from("refreshToken", cookieRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL + "/auth")
                .maxAge(0)
                .sameSite("None")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", cookieAccessToken)
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL)
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString()).build();
    }

    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCookies(@CookieValue(name = "refreshToken") String cookieRefreshToken,
                                                          @CookieValue(name = "accessToken") String cookieAccessToken) {
        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(cookieRefreshToken);
        if (refreshToken.isPresent())
            throw new GenericException(ApiResponse.error("INVALID_AUTH_STATE", "Cannot clear cookies!"));

        ResponseCookie refCookie = ResponseCookie.from("refreshToken", cookieRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL + "/auth")
                .maxAge(0)
                .sameSite("None")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", cookieAccessToken)
                .httpOnly(true)
                .secure(true)
                .path(JNotifierConstants.API_BASE_URL)
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString()).build();
    }

    // --- Helper Methods ---

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

    private String generateCaptchaImage(String captchaCode) {
        try {
            int width = 130;
            int height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Draw background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Draw noisy lines
            Random rnd = new Random();
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 5; i++) {
                g2d.drawLine(rnd.nextInt(width), rnd.nextInt(height), rnd.nextInt(width), rnd.nextInt(height));
            }

            // Draw text
            g2d.setColor(new Color(33, 150, 243));
            g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
            g2d.drawString(captchaCode, 15, 28);

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64Image;
        } catch (IOException e) {
            throw new RuntimeException("Error generating captcha image", e);
        }
    }
}
