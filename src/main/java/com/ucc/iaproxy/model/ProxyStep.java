package com.ucc.iaproxy.model;

/**
 * One link of the proxy chain that a request went through.
 * Lets the client see the exact path: RateLimit -> Quota -> AI service.
 *
 * @param layer      name of the layer (for example "RateLimitProxy")
 * @param detail     what that layer checked or did
 * @param durationMs time spent in the layer itself, in milliseconds
 */
public record ProxyStep(String layer, String detail, long durationMs) {
}
