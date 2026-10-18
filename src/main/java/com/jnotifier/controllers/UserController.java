package com.jnotifier.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.impl.UserServices;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServices services;

    @Autowired
    private com.jnotifier.services.core.ProfilePictureService profilePictureService;

    @GetMapping("/get-me")
    public ResponseEntity<ApiResponse<Object>> getMe(Authentication authentication) {
        Map<String, String> reply = new HashMap<>();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        reply.put("message", "Authenticated");
        reply.put("role", role);
        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getProfile(Authentication authentication) {
        Map<String, String> reply = new HashMap<>();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new GenericException(ApiResponse.error(
                "INVALID_CREDS", "Invalid credentials"
        )));

        reply.put("fullName", user.getFullname());
        reply.put("email", user.getEmail());
        reply.put("username", user.getUsername());
        reply.put("category", user.getCategory());
        reply.put("isPwd", Optional.ofNullable(user.getIsPwd()).orElse(false).toString());
        reply.put("mobile", user.getMobile());
        reply.put("dob", user.getDob() != null ? user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        reply.put("gender", user.getGender());
        reply.put("companyName", user.getCompanyName());
        reply.put("address", user.getAddress());
        reply.put("profilePicture", profilePictureService.getProfilePictureDataUrl(user));

        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<ApiResponse<Object>> uploadProfilePicture(Authentication authentication,
                                                                    @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new GenericException(ApiResponse.error(
                "INVALID_CREDS", "Invalid credentials"
        )));

        profilePictureService.saveEncryptedProfilePicture(file, user);
        String dataUrl = profilePictureService.getProfilePictureDataUrl(user);

        Map<String, String> reply = new HashMap<>();
        reply.put("message", "Profile picture uploaded successfully");
        reply.put("profilePicture", dataUrl);
        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<byte[]> getDecryptedProfilePicture(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new GenericException(ApiResponse.error(
                "INVALID_CREDS", "Invalid credentials"
        )));

        com.jnotifier.services.core.ProfilePictureService.DecryptedPhoto photo = profilePictureService.getDecryptedProfilePicture(user);
        if (photo == null || photo.bytes() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(photo.contentType()))
                .body(photo.bytes());
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<ApiResponse<Object>> deleteProfilePicture(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new GenericException(ApiResponse.error(
                "INVALID_CREDS", "Invalid credentials"
        )));

        profilePictureService.deleteProfilePicture(user);
        Map<String, String> reply = new HashMap<>();
        reply.put("message", "Profile picture deleted successfully");
        return ResponseEntity.ok(ApiResponse.success(reply));
    }


    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<Object>> registerAdminUsers(@Valid @RequestBody SignupRequest signupRequest) throws JsonProcessingException, GenericException {
        ServiceReply serviceReply = services.registerAdminUsers(signupRequest);
        return ResponseEntity.status(serviceReply.getHttpStatusCode()).body(ApiResponse.success(serviceReply.getReply()));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAllUserDetailsExceptSA(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size)
            throws GenericException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ServiceReply reply = services.getAllUsersDetailsExceptSA(pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @PatchMapping("/mark/suspended/{userId}")
    public ResponseEntity<ApiResponse<?>> markUserAsSuspended(@PathVariable Long userId) throws GenericException {
        ServiceReply serviceReply = services.markUserAsSuspended(userId);
        return ResponseEntity.status(serviceReply.getHttpStatusCode()).build();
    }

    @PatchMapping("/mark/activate/{userId}")
    public ResponseEntity<ApiResponse<?>> activateUser(@PathVariable Long userId) throws GenericException {
        ServiceReply serviceReply = services.activateUser(userId);
        return ResponseEntity.status(serviceReply.getHttpStatusCode()).build();
    }

    @DeleteMapping("/mark/deleted/{userId}")
    public ResponseEntity<ApiResponse<?>> markUserAsDeleted(@PathVariable Long userId) throws GenericException {
        ServiceReply serviceReply = services.markUserAsDeleted(userId);
        return ResponseEntity.status(serviceReply.getHttpStatusCode()).build();
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<Void> editProfile(Authentication authentication, @Valid @RequestBody com.jnotifier.payload.request.EditProfileRequest request) throws GenericException {
        ServiceReply reply = services.editProfile(authentication.getName(), request);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }
}
