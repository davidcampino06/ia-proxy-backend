package com.ucc.iaproxy.service;

/**
 * Simple token estimate: roughly 1 token for every 4 characters.
 * The frontend uses the same formula for its estimator.
 */
public final class TokenEstimator {

    private TokenEstimator() {
    }

    public static long estimate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (long) Math.ceil(text.length() / 4.0);
    }
}
