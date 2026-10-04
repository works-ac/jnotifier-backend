package com.jnotifier.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jnotifier.payload.response.ApiError;
import com.jnotifier.payload.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleTokenRefreshException(TokenRefreshException ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] Token refresh error occurred: {}", requestId, ex.getMessage(), ex);
        return ApiResponse.error("TOKEN_REFRESH_ERROR", ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] Token refresh error occurred: {}", requestId, ex.getMessage(), ex);
        return ApiResponse.error("TOKEN_REFRESH_EXPIRED", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleBadCredentialException(TokenRefreshException ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] Bad credential error occurred: {}", requestId, ex.getMessage(), ex);
        return ApiResponse.error("BAD_CREDS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] Access Denied: {}", requestId, ex.getMessage(), ex);
        return ApiResponse.error("ACCESS_DENIED", "You do not have permission to access this resource.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String requestId = MDC.get("requestId");
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        logger.error("[Request ID: {}] Validation failed: {}", requestId, details);

        ApiError apiError = new ApiError("VALIDATION_FAILED", "Input validation failed.", details);
        return ApiResponse.error(apiError);
    }

    @ExceptionHandler(GenericException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleGenericException(GenericException ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] Generic error occurred: {}", requestId, ex.getMessage());
        return ex.getResponseMap();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        String requestId = MDC.get("requestId");
        logger.error("[Request ID: {}] An unexpected error occurred: {}", requestId, ex.getMessage(), ex);
        return ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please contact support.");
    }
}
