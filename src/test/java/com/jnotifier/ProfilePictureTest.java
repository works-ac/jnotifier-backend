package com.jnotifier;

import com.jnotifier.entity.User;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.core.ProfilePictureService;
import com.jnotifier.services.impl.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfilePictureTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfilePictureService profilePictureService;

    private static final String TEST_USERNAME = "test_avatar_user";
    private User testUser;

    @BeforeEach
    public void setup() {
        userRepository.findByUsername(TEST_USERNAME).ifPresent(u -> {
            profilePictureService.deleteProfilePicture(u);
            userRepository.delete(u);
        });

        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail("avatar_test@jnotifier.com");
        testUser.setPassword("Password123!");
        testUser.setFullname("Avatar Test User");
        testUser.setGender("M");
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    public void cleanup() {
        if (testUser != null) {
            userRepository.findByUsername(TEST_USERNAME).ifPresent(u -> {
                profilePictureService.deleteProfilePicture(u);
                userRepository.delete(u);
            });
        }
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, roles = "ADMIN")
    public void testUploadAndDecryptionAtRest() throws Exception {
        byte[] fakeImageBytes = new byte[1024];
        for (int i = 0; i < fakeImageBytes.length; i++) {
            fakeImageBytes[i] = (byte) (i % 128);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                fakeImageBytes
        );

        // 1. Upload photo
        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/users/profile-picture").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.profilePicture").exists())
                .andReturn();

        // 2. Verify DB state
        User updatedUser = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertThat(updatedUser.getProfilePicture()).isNotNull();
        assertThat(updatedUser.getProfilePictureType()).isEqualTo("image/png");

        // 3. Verify ENCRYPTION AT REST on filesystem:
        // Ciphertext on disk must NOT equal the original plaintext image bytes
        Path storedFile = Paths.get("uploads", "avatars", updatedUser.getProfilePicture());
        assertThat(Files.exists(storedFile)).isTrue();
        byte[] onDiskBytes = Files.readAllBytes(storedFile);
        assertThat(Arrays.equals(onDiskBytes, fakeImageBytes)).isFalse();
        // Encrypted data contains 12-byte IV + 16-byte GCM tag + ciphertext
        assertThat(onDiskBytes.length).isEqualTo(fakeImageBytes.length + 12 + 16);

        // 4. Verify DECRYPTION on retrieval via GET /api/v1/users/profile-picture
        MvcResult streamResult = mockMvc.perform(get("/api/v1/users/profile-picture"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andReturn();

        byte[] returnedBytes = streamResult.getResponse().getContentAsByteArray();
        assertThat(returnedBytes).isEqualTo(fakeImageBytes);

        // 5. Verify GET /api/v1/users/profile includes base64 Data URL
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.profilePicture").value(org.hamcrest.Matchers.startsWith("data:image/png;base64,")));

        // 6. Delete profile picture
        mockMvc.perform(delete("/api/v1/users/profile-picture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        User postDeleteUser = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertThat(postDeleteUser.getProfilePicture()).isNull();
        assertThat(postDeleteUser.getProfilePictureType()).isNull();
        assertThat(Files.exists(storedFile)).isFalse();

        // 7. GET after deletion returns 404
        mockMvc.perform(get("/api/v1/users/profile-picture"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, roles = "ADMIN")
    public void testRejectFileExceeding1MB() throws Exception {
        // Exceed 1MB (1024 * 1024 + 10 bytes)
        byte[] largeBytes = new byte[1024 * 1024 + 10];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                largeBytes
        );

        mockMvc.perform(multipart("/api/v1/users/profile-picture").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_FILE_SIZE"));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, roles = "ADMIN")
    public void testRejectDisallowedFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "dummy pdf bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/users/profile-picture").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FILE_TYPE_ERR"));
    }
}
