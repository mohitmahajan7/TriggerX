package org.stark.triggerxbackend.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<?> invalidOtp(OtpInvalidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getMessage(),
                "type", "OTP_INVALID"
        ));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<?> expiredOtp(OtpExpiredException ex) {
        return ResponseEntity.status(410).body(Map.of(
                "error", ex.getMessage(),
                "type", "OTP_EXPIRED"
        ));
    }

    @ExceptionHandler(OtpLockedException.class)
    public ResponseEntity<?> lockedOtp(OtpLockedException ex) {
        return ResponseEntity.status(429).body(Map.of(
                "error", ex.getMessage(),
                "retryAfterSeconds", ex.getRetryAfterSeconds(),
                "type", "OTP_ATTEMPT_LIMIT"
        ));
    }
}

