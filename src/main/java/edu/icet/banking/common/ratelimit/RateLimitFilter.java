package edu.icet.banking.common.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icet.banking.common.exception.ErrorResponse;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> RATE_LIMITED_PATHS = Set.of(
            "/auth/login", "/auth/register", "/auth/google"
    );

    private static final Set<String> EMAIL_CHECKED_PATHS = Set.of(
            "/auth/login", "/auth/register"
    );

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return RATE_LIMITED_PATHS.stream().noneMatch(path::endsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        String ip = resolveClientIp(request);

        ConsumptionProbe ipProbe = rateLimiterService.tryConsumeIp(ip);
        if (!ipProbe.isConsumed()) {
            long retryAfter = TimeUnit.NANOSECONDS.toSeconds(ipProbe.getNanosToWaitForRefill());
            log.warn("Rate limit exceeded for IP={} on path={}", ip, request.getServletPath());
            writeRateLimitResponse(response, retryAfter,
                    "Too many requests from your IP address. Try again in " + retryAfter + " seconds.");
            return;
        }

        String path = request.getServletPath();
        if (EMAIL_CHECKED_PATHS.stream().anyMatch(path::endsWith)) {
            String email = extractEmail(wrappedRequest);
            if (email != null && !email.isBlank()) {
                ConsumptionProbe emailProbe = rateLimiterService.tryConsumeEmail(email);
                if (!emailProbe.isConsumed()) {
                    long retryAfter = TimeUnit.NANOSECONDS.toSeconds(emailProbe.getNanosToWaitForRefill());
                    log.warn("Rate limit exceeded for email={} on path={}", email, path);
                    writeRateLimitResponse(response, retryAfter,
                            "Too many attempts for this account. Try again in " + retryAfter + " seconds.");
                    return;
                }
            }
        }

        filterChain.doFilter(wrappedRequest, response);
    }
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For may be a comma-separated list; the first value is the original client
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
    private String extractEmail(ContentCachingRequestWrapper request) {
        try {
            byte[] body = request.getInputStream().readAllBytes();
            if (body.length == 0) {
                return null;
            }
            JsonNode node = objectMapper.readTree(body);
            JsonNode emailNode = node.get("email");
            return (emailNode != null && !emailNode.isNull()) ? emailNode.asText() : null;
        } catch (Exception e) {
            log.debug("Could not extract email from request body: {}", e.getMessage());
            return null;
        }
    }
    private void writeRateLimitResponse(HttpServletResponse response,
                                        long retryAfterSeconds,
                                        String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

        ErrorResponse body = ErrorResponse.builder()
                .errorCode("RATE_LIMIT_EXCEEDED")
                .message(message)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}
