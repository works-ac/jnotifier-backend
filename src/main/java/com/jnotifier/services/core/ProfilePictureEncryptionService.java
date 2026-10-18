package com.jnotifier.services.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * ProfilePictureEncryptionService
 *
 * Provides AES-256-GCM authenticated encryption and decryption for user profile pictures.
 * Each encryption operation uses a cryptographically secure, random 12-byte (96-bit) IV.
 * The resulting payload stored on disk is:
 *   [ 12 bytes IV ] + [ Ciphertext + 16 bytes GCM Auth Tag ]
 */
@Service
public class ProfilePictureEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public ProfilePictureEncryptionService(
            @Value("${jnotifier.app.profilePictureSecretKey:${jnotifier.app.jwtSecret:JobNotifierDefaultSecureSecretKey2026ProfilePicture}}")
            String rawSecret) {
        try {
            // Derive a consistent 256-bit (32 bytes) AES key using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(rawSecret.getBytes(StandardCharsets.UTF_8));
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize profile picture encryption key", e);
        }
    }

    /**
     * Encrypts plaintext photo bytes using AES-256-GCM.
     * Prepend 12-byte IV to the returned ciphertext.
     *
     * @param plainBytes raw image bytes
     * @return [12-byte IV] + [Ciphertext + Tag]
     */
    public byte[] encrypt(byte[] plainBytes) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainBytes);

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return combined;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt profile picture", e);
        }
    }

    /**
     * Decrypts AES-256-GCM payload into raw image bytes.
     *
     * @param encryptedBytes [12-byte IV] + [Ciphertext + Tag]
     * @return original plaintext image bytes
     */
    public byte[] decrypt(byte[] encryptedBytes) {
        try {
            if (encryptedBytes == null || encryptedBytes.length <= GCM_IV_LENGTH_BYTES) {
                throw new IllegalArgumentException("Invalid encrypted payload size");
            }

            byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, GCM_IV_LENGTH_BYTES);
            byte[] cipherText = Arrays.copyOfRange(encryptedBytes, GCM_IV_LENGTH_BYTES, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt profile picture", e);
        }
    }
}
