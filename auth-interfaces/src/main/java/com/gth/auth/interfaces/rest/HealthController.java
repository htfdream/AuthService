package com.gth.auth.interfaces.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthIndicator healthIndicator;

    public HealthController(@Qualifier("pingHealthContributor") HealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @GetMapping("/health")
    public Health health() {
        return healthIndicator.health();
    }

    @GetMapping("/ready")
    public String ready() {
        return "ready";
    }

    @GetMapping("/live")
    public String live() {
        return "alive";
    }
}
