package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnotifier.controllers.AuthController;
import com.jnotifier.helpers.CaptchaHelper;
import com.jnotifier.helpers.OTPHelper;
import com.jnotifier.payload.request.LoginRequest;
import com.jnotifier.payload.request.OtpRequest;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.entity.User;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.repository.RefreshTokenRepository;

import org.springframework.boot.test.mock.mockito.MockBean;
import com.jnotifier.services.impl.RedisService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RedisService redisService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void testCaptchaGeneration() throws Exception {
    mockMvc.perform(get("/api/v1/auth/captcha"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.captchaId").exists())
        .andExpect(jsonPath("$.data.captchaImage").value(org.hamcrest.Matchers.startsWith("data:image/png;base64,")))
        .andExpect(jsonPath("$.requestId").exists());
  }

  @Test
  public void testUserSignupAndLoginFlow() throws Exception {
    // 1. Get Captcha Id & Code
    MvcResult captchaResult = mockMvc.perform(get("/api/v1/auth/captcha"))
        .andExpect(status().isOk())
        .andReturn();

    String captchaResponseStr = captchaResult.getResponse().getContentAsString();
    Map<?, ?> responseMap = objectMapper.readValue(captchaResponseStr, Map.class);
    Map<?, ?> dataMap = (Map<?, ?>) responseMap.get("data");
    String captchaId = (String) dataMap.get("captchaId");

    // Fetch the stored captcha code using reflection
    Field captchaStoreField = CaptchaHelper.class.getDeclaredField("captchaStore");
    captchaStoreField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, String> captchaStore = (Map<String, String>) captchaStoreField.get(null);
    String captchaValue = captchaStore.get(captchaId);
    assertThat(captchaValue).isNotNull();

    // 2. Signup public user (default: superadmin role)
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setFullName("John Doe");
    signupRequest.setEmail("john.doe@example.com");
    signupRequest.setPassword("securePassword123");
    signupRequest.setDob(LocalDate.of(1990, 1, 1));
    signupRequest.setGender("M");
    signupRequest.setMobile("1234567890");
    signupRequest.setRole("user");
    signupRequest.setIsPwd(false);
    signupRequest.setCategory("GEN");
    signupRequest.setCaptchaId(captchaId);
    signupRequest.setCaptchaValue(captchaValue);

    MvcResult signupResult = mockMvc.perform(post("/api/v1/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signupRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("User successfully registered!")))
        .andExpect(jsonPath("$.requestId").exists())
        .andReturn();

    String signupResponseStr = signupResult.getResponse().getContentAsString();
    Map<?, ?> signupResponseMap = objectMapper.readValue(signupResponseStr, Map.class);
    Map<?, ?> signupDataMap = (Map<?, ?>) signupResponseMap.get("data");
    String generatedUsername = (String) signupDataMap.get("username");
    assertThat(generatedUsername).isNotEmpty();

    // Verify email for login
    User registeredUser = userRepository.findByUsername(generatedUsername).orElseThrow();
    registeredUser.setIsEmailVerified(true);
    userRepository.save(registeredUser);

    // Get a new captcha for Signin
    Map<String, String> signinCaptcha = getNewCaptcha();
    String signinCaptchaId = signinCaptcha.get("captchaId");
    String signinCaptchaValue = signinCaptcha.get("captchaValue");

    // 3. Try login with incorrect captcha
    LoginRequest badLoginRequest = new LoginRequest();
    badLoginRequest.setUsername(generatedUsername);
    badLoginRequest.setPassword("securePassword123");
    badLoginRequest.setCaptchaId(signinCaptchaId);
    badLoginRequest.setCaptchaValue("WRONG_CAPTCHA");

    mockMvc.perform(post("/api/v1/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(badLoginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("INVALID_CAPTCHA"));

    // 4. Login with correct captcha, triggers OTP
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername(generatedUsername);
    loginRequest.setPassword("securePassword123");
    loginRequest.setCaptchaId(signinCaptchaId);
    loginRequest.setCaptchaValue(signinCaptchaValue);

    mockMvc.perform(post("/api/v1/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("OTP_REQUIRED"))
        .andExpect(jsonPath("$.data.username").value(generatedUsername));

    // Fetch the generated OTP code using reflection
    Field otpStoreField = OTPHelper.class.getDeclaredField("otpStore");
    otpStoreField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, String> otpStore = (Map<String, String>) otpStoreField.get(null);
    String otpCode = otpStore.get(generatedUsername);
    assertThat(otpCode).isNotNull();

    // 5. Try OTP verification with incorrect OTP
    OtpRequest badOtpRequest = new OtpRequest();
    badOtpRequest.setUsername(generatedUsername);
    badOtpRequest.setOtpCode("000000");
    badOtpRequest.setVerificationType("LOGIN");

    mockMvc.perform(post("/api/v1/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(badOtpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("INVALID_OTP"));

    // 6. Complete login with correct OTP
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setUsername(generatedUsername);
    otpRequest.setOtpCode(otpCode);
    otpRequest.setVerificationType("LOGIN");

    mockMvc.perform(post("/api/v1/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(otpRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.accessToken").exists())
        .andExpect(jsonPath("$.data.refreshToken").exists())
        .andExpect(jsonPath("$.data.username").value(generatedUsername))
        .andExpect(jsonPath("$.data.roles[0]").value("ROLE_USER"))
        .andExpect(cookie().exists("refreshToken"))
        .andExpect(cookie().httpOnly("refreshToken", true));
  }

  @Test
  public void testAdminRoleCreationRestrictions() throws Exception {
    Map<String, String> captcha = getNewCaptcha();
    SignupRequest adminSignupRequest = new SignupRequest();
    adminSignupRequest.setEmail("admin@example.com");
    adminSignupRequest.setFullName("Admin User");
    adminSignupRequest.setPassword("securePassword123");
    adminSignupRequest.setDob(LocalDate.of(1985, 5, 5));
    adminSignupRequest.setGender("F");
    adminSignupRequest.setMobile("9876543210");
    adminSignupRequest.setRole("admin");
    adminSignupRequest.setIsPwd(false);
    adminSignupRequest.setCategory("GEN");
    adminSignupRequest.setCompanyName("CodingWorks");
    adminSignupRequest.setAddress("Bangalore");
    adminSignupRequest.setCaptchaId(captcha.get("captchaId"));
    adminSignupRequest.setCaptchaValue(captcha.get("captchaValue"));

    // Unauthenticated request should fail to create admin
    mockMvc.perform(post("/api/v1/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(adminSignupRequest)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  @WithMockUser(authorities = "ROLE_SUPERADMIN")
  public void testAdminRoleCreationBySuperadmin() throws Exception {
    Map<String, String> captcha = getNewCaptcha();
    SignupRequest adminSignupRequest = new SignupRequest();
    adminSignupRequest.setFullName("Admin User");
    adminSignupRequest.setEmail("admin@example.com");
    adminSignupRequest.setPassword("securePassword123");
    adminSignupRequest.setDob(LocalDate.of(1985, 5, 5));
    adminSignupRequest.setGender("F");
    adminSignupRequest.setMobile("9876543210");
    adminSignupRequest.setRole("admin");
    adminSignupRequest.setIsPwd(false);
    adminSignupRequest.setCategory("GEN");
    adminSignupRequest.setCompanyName("CodingWorks");
    adminSignupRequest.setAddress("Bangalore");
    adminSignupRequest.setCaptchaId(captcha.get("captchaId"));
    adminSignupRequest.setCaptchaValue(captcha.get("captchaValue"));

    // Authenticated request with ROLE_SUPERADMIN should succeed
    mockMvc.perform(post("/api/v1/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(adminSignupRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  public void testRefreshTokenFromCookieFlow() throws Exception {
    // 1. Get Captcha
    MvcResult captchaResult = mockMvc.perform(get("/api/v1/auth/captcha"))
        .andExpect(status().isOk())
        .andReturn();
    String captchaResponseStr = captchaResult.getResponse().getContentAsString();
    Map<?, ?> responseMap = objectMapper.readValue(captchaResponseStr, Map.class);
    Map<?, ?> dataMap = (Map<?, ?>) responseMap.get("data");
    String captchaId = (String) dataMap.get("captchaId");

    Field captchaStoreField = CaptchaHelper.class.getDeclaredField("captchaStore");
    captchaStoreField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, String> captchaStore = (Map<String, String>) captchaStoreField.get(null);
    String captchaValue = captchaStore.get(captchaId);

    // 2. Signup
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setFullName("Cookie Tester");
    signupRequest.setEmail("cookie.tester@example.com");
    signupRequest.setPassword("cookiePass123");
    signupRequest.setDob(LocalDate.of(1990, 1, 1));
    signupRequest.setGender("M");
    signupRequest.setMobile("1234567890");
    signupRequest.setRole("user");
    signupRequest.setIsPwd(false);
    signupRequest.setCategory("GEN");
    signupRequest.setCaptchaId(captchaId);
    signupRequest.setCaptchaValue(captchaValue);

    MvcResult signupResult = mockMvc.perform(post("/api/v1/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signupRequest)))
        .andExpect(status().isOk())
        .andReturn();
    String signupResponseStr = signupResult.getResponse().getContentAsString();
    Map<?, ?> signupResponseMap = objectMapper.readValue(signupResponseStr, Map.class);
    Map<?, ?> signupDataMap = (Map<?, ?>) signupResponseMap.get("data");
    String generatedUsername = (String) signupDataMap.get("username");

    // Verify email for login
    User registeredUser = userRepository.findByUsername(generatedUsername).orElseThrow();
    registeredUser.setIsEmailVerified(true);
    userRepository.save(registeredUser);

    // Get a new captcha for Signin
    Map<String, String> signinCaptcha = getNewCaptcha();
    String signinCaptchaId = signinCaptcha.get("captchaId");
    String signinCaptchaValue = signinCaptcha.get("captchaValue");

    // 3. Signin
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername(generatedUsername);
    loginRequest.setPassword("cookiePass123");
    loginRequest.setCaptchaId(signinCaptchaId);
    loginRequest.setCaptchaValue(signinCaptchaValue);

    mockMvc.perform(post("/api/v1/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk());

    Field otpStoreField = OTPHelper.class.getDeclaredField("otpStore");
    otpStoreField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, String> otpStore = (Map<String, String>) otpStoreField.get(null);
    String otpCode = otpStore.get(generatedUsername);

    // 4. Verify OTP (Gets Cookie)
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setUsername(generatedUsername);
    otpRequest.setOtpCode(otpCode);
    otpRequest.setVerificationType("LOGIN");

    MvcResult verifyResult = mockMvc.perform(post("/api/v1/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(otpRequest)))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("refreshToken"))
        .andReturn();

    jakarta.servlet.http.Cookie refreshCookie = verifyResult.getResponse().getCookie("refreshToken");
    assertThat(refreshCookie).isNotNull();

    // 5. Refresh token using cookie
    mockMvc.perform(post("/api/v1/auth/refresh-token")
        .cookie(refreshCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.accessToken").exists())
        .andExpect(cookie().exists("refreshToken"));
  }

  private Map<String, String> getNewCaptcha() throws Exception {
    MvcResult captchaResult = mockMvc.perform(get("/api/v1/auth/captcha"))
        .andExpect(status().isOk())
        .andReturn();

    String captchaResponseStr = captchaResult.getResponse().getContentAsString();
    Map<?, ?> responseMap = objectMapper.readValue(captchaResponseStr, Map.class);
    Map<?, ?> dataMap = (Map<?, ?>) responseMap.get("data");
    String captchaId = (String) dataMap.get("captchaId");

    // Fetch the stored captcha code using reflection
    Field captchaStoreField = CaptchaHelper.class.getDeclaredField("captchaStore");
    captchaStoreField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, String> captchaStore = (Map<String, String>) captchaStoreField.get(null);
    String captchaValue = captchaStore.get(captchaId);
    
    Map<String, String> result = new HashMap<>();
    result.put("captchaId", captchaId);
    result.put("captchaValue", captchaValue);
    return result;
  }
}
