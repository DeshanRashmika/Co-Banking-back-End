package edu.icet.banking.common.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class RateLimiterService {

    private static final int IP_CAPACITY        = 20;
    private static final Duration IP_REFILL     = Duration.ofMinutes(1);

    private static final int EMAIL_CAPACITY     = 5;
    private static final Duration EMAIL_REFILL  = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, Bucket> ipBuckets    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> emailBuckets = new ConcurrentHashMap<>();
    public ConsumptionProbe tryConsumeIp(String ip) {
        return bucketForIp(ip).tryConsumeAndReturnRemaining(1);
    }
    public ConsumptionProbe tryConsumeEmail(String email) {
        return bucketForEmail(email).tryConsumeAndReturnRemaining(1);
    }

    private Bucket bucketForIp(String ip) {
        return ipBuckets.computeIfAbsent(ip, k -> buildBucket(IP_CAPACITY, IP_REFILL));
    }

    private Bucket bucketForEmail(String email) {
        return emailBuckets.computeIfAbsent(email.toLowerCase(), k -> buildBucket(EMAIL_CAPACITY, EMAIL_REFILL));
    }

    private Bucket buildBucket(int capacity, Duration refillPeriod) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, refillPeriod)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
