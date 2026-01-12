package org.stark.triggerxbackend.common.exception;


public class OtpLockedException extends RuntimeException {

    private final long retryAfterSeconds;

    public OtpLockedException(long retryAfterSeconds) {
        super("OTP attempts exceeded");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

