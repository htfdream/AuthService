package com.gth.auth.interfaces.grpc.config;

import com.gth.auth.infrastructure.ratelimit.RateLimitService;
import com.gth.auth.interfaces.grpc.interceptor.RateLimitingInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class GrpcConfig {

    @Bean
    @GrpcGlobalServerInterceptor
    @Order(1)  // Высокий приоритет — выполняется первым
    public RateLimitingInterceptor rateLimitingInterceptor(RateLimitService rateLimitService) {
        return new RateLimitingInterceptor(rateLimitService);
    }
}
