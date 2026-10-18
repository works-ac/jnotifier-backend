package com.jnotifier.services.core;

import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * ProfilePictureService
 *
 * Coordinates file validation (1MB max, image types), AES-256-GCM encryption on upload,
 * secure on-disk encrypted file management in uploads/avatars, and in-memory decryption.
 */
@Service
public class ProfilePictureService {

    private static final Logger logger = LoggerFactory.getLogger(ProfilePictureService.class);

    public static final long MAX_PROFILE_PICTURE_SIZE_BYTES = 1024 * 1024; // Exactly 1MB
    private static final String ALLOWED_CONTENT_TYPES_REGEX = "^(image/(jpeg|jpg|png|webp))$";

    private final Path avatarsLocation = Paths.get("uploads", "avatars");

    @Autowired
    private ProfilePictureEncryptionService encryptionService;

    @Autowired
    private UserRepository userRepository;

    public record DecryptedPhoto(byte[] bytes, String contentType) {}

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(avatarsLocation);
        } catch (IOException e) {
            logger.error("Failed to create avatars directory", e);
            throw new RuntimeException("Could not initialize avatars storage directory", e);
        }
    }

    /**
     * Validates, encrypts and stores a new profile picture for the given user.
     * Deletes the user's previous encrypted avatar from disk if one existed.
     */
    public String saveEncryptedProfilePicture(MultipartFile file, User user) {
        if (file == null || file.isEmpty()) {
            throw new GenericException(ApiResponse.error("FILE_ERR", "Profile picture file is required."));
        }

        if (file.getSize() > MAX_PROFILE_PICTURE_SIZE_BYTES) {
            throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Profile picture size must not exceed 1MB."));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().matches(ALLOWED_CONTENT_TYPES_REGEX)) {
            throw new GenericException(ApiResponse.error("FILE_TYPE_ERR", "Only JPG, PNG, and WEBP image files are accepted."));
        }

        try {
            byte[] plainBytes = file.getBytes();
            byte[] encryptedBytes = encryptionService.encrypt(plainBytes);

            String filename = UUID.randomUUID() + ".enc";
            Path targetFile = avatarsLocation.resolve(filename).normalize().toAbsolutePath();

            if (!targetFile.getParent().equals(avatarsLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside avatars directory.");
            }

            Files.write(targetFile, encryptedBytes);

            // Clean up old encrypted avatar if one exists
            deleteOldFileQuietly(user.getProfilePicture());

            user.setProfilePicture(filename);
            user.setProfilePictureType(contentType.toLowerCase());
            userRepository.save(user);

            return filename;
        } catch (IOException e) {
            logger.error("Failed to save encrypted profile picture for user {}", user.getUsername(), e);
            throw new RuntimeException("Failed to store profile picture.", e);
        }
    }

    /**
     * Reads the encrypted avatar from disk and decrypts it into memory.
     *
     * @param user target user
     * @return DecryptedPhoto containing raw image bytes and MIME type, or null if no picture set
     */
    public DecryptedPhoto getDecryptedProfilePicture(User user) {
        if (user == null || user.getProfilePicture() == null || user.getProfilePicture().trim().isEmpty()) {
            return null;
        }

        Path filePath = avatarsLocation.resolve(user.getProfilePicture()).normalize().toAbsolutePath();
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            logger.warn("Profile picture file not found on disk: {}", filePath);
            return null;
        }

        try {
            byte[] encryptedBytes = Files.readAllBytes(filePath);
            byte[] decryptedBytes = encryptionService.decrypt(encryptedBytes);
            return new DecryptedPhoto(decryptedBytes, user.getProfilePictureType());
        } catch (Exception e) {
            logger.error("Failed to decrypt profile picture for user {}", user.getUsername(), e);
            throw new GenericException(ApiResponse.error("DECRYPTION_ERR", "Failed to decrypt profile picture."));
        }
    }

    /**
     * Returns the decrypted profile picture as a Base64 Data URL (e.g. data:image/png;base64,...),
     * or null if the user does not have a profile picture.
     */
    public String getProfilePictureDataUrl(User user) {
        DecryptedPhoto photo = getDecryptedProfilePicture(user);
        if (photo == null || photo.bytes() == null || photo.bytes().length == 0) {
            return null;
        }
        String mime = photo.contentType() != null ? photo.contentType() : "image/jpeg";
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(photo.bytes());
    }

    /**
     * Deletes the user's profile picture from disk and clears entity reference.
     */
    public void deleteProfilePicture(User user) {
        if (user == null) return;

        deleteOldFileQuietly(user.getProfilePicture());
        user.setProfilePicture(null);
        user.setProfilePictureType(null);
        userRepository.save(user);
    }

    private void deleteOldFileQuietly(String filename) {
        if (filename != null && !filename.trim().isEmpty()) {
            try {
                Path oldFile = avatarsLocation.resolve(filename).normalize().toAbsolutePath();
                Files.deleteIfExists(oldFile);
            } catch (Exception e) {
                logger.warn("Could not delete old profile picture {}: {}", filename, e.getMessage());
            }
        }
    }
}
