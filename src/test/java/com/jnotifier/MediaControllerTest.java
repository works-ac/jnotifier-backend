package com.jnotifier;

import com.jnotifier.helpers.CaptchaHelper;
import com.jnotifier.services.impl.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisService redisService;

    @Autowired
    private CaptchaHelper captchaHelper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddMediaWithInvalidCaptcha() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.pdf",
                "application/pdf",
                "sample content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/media/add")
                        .file(file)
                        .param("captchaId", "non-existent-id")
                        .param("captcha", "wrong-code"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CAPTCHA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddMediaWithValidCaptcha() throws Exception {
        String testCaptchaId = "test-captcha-id-media";
        String testCaptchaCode = "AB12CD";
        captchaHelper.generateCaptcha(testCaptchaId, testCaptchaCode);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.pdf",
                "application/pdf",
                "sample pdf file content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/media/add")
                        .file(file)
                        .param("captchaId", testCaptchaId)
                        .param("captcha", testCaptchaCode))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fileUri").exists());
    }
}
