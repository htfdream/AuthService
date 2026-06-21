package com.gth.auth.interfaces.grpc.interceptor;

import com.gth.auth.infrastructure.ratelimit.RateLimitService;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Set;

@Slf4j
public class RateLimitingInterceptor implements ServerInterceptor {
    private final RateLimitService rateLimitService;

    private static final int DEFAULT_LIMIT = 100;
    private static final Duration DEFAULT_WINDOW = Duration.ofMinutes(1);

    private static final int LOGIN_LIMIT = 10;
    private static final int REGISTER_LIMIT = 5;

    private static final Set<String> SENSITIVE_METHODS = Set.of(
            "com.gth.auth.v1.AuthService/Login",
            "com.gth.auth.v1.AuthService/Register"
    );


    public RateLimitingInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String methodName = serverCall.getMethodDescriptor().getFullMethodName();
        String clientIp = extractClientIp(serverCall, metadata);
        String key = clientIp + ":" + methodName;

        int limit = DEFAULT_LIMIT;
        if (methodName.contains("Login")) {
            limit = LOGIN_LIMIT;
        } else if (methodName.contains("Register")) {
            limit = REGISTER_LIMIT;
        }

        // Проверка
        if (!rateLimitService.isAllowed(key, limit, DEFAULT_WINDOW)) {
            log.warn("Rate limit exceeded: IP={}, method={}, limit={}", clientIp, methodName, limit);

            Metadata trailers = new Metadata();
            Metadata.Key<String> retryAfterKey =
                    Metadata.Key.of("retry-after", Metadata.ASCII_STRING_MARSHALLER);
            trailers.put(retryAfterKey, "60");

            serverCall.close(
                    Status.RESOURCE_EXHAUSTED
                            .withDescription("Too many requests. Rate limit exceeded. Try again in 60 seconds."),
                    trailers
            );
            return new ServerCall.Listener<>() {};
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }

    private <ReqT, RespT> String extractClientIp(ServerCall<ReqT,RespT> serverCall, Metadata metadata) {
        // 1. Пробуем взять из x-forwarded-for (если есть прокси)
        Metadata.Key<String> xForwardedForKey =
                Metadata.Key.of("x-forwarded-for", Metadata.ASCII_STRING_MARSHALLER);
        String forwarded = metadata.get(xForwardedForKey);

        if (forwarded != null && !forwarded.isEmpty()) {
            // Берем первый IP из списка
            return forwarded.split(",")[0].trim();
        }

        // 2. Пробуем взять из peer (реальный IP)
        InetSocketAddress address = (InetSocketAddress)
                serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);

        if (address != null) {
            return address.getAddress().getHostAddress();
        }

        // 3. Fallback
        return "unknown";
    }
}
