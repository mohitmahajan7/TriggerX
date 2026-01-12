package org.stark.triggerxbackend.common.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException() {
        super("OTP expired");
    }
}
