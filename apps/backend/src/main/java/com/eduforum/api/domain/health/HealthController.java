package com.eduforum.api.domain.health;

import com.eduforum.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 헬스 체크 API
 */
@Tag(name = "Health", description = "헬스 체크 API")
@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @Value("${spring.application.name:EduForum API}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * 기본 헬스 체크
     */
    @Operation(summary = "헬스 체크", description = "API 서버의 상태를 확인합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("profile", activeProfile);
        health.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(health));
    }

    /**
     * Readiness 프로브 (Kubernetes용)
     */
    @Operation(summary = "Readiness 체크", description = "서비스가 요청을 받을 준비가 되었는지 확인합니다")
    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ready() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "READY");
        status.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(status));
    }

    /**
     * Liveness 프로브 (Kubernetes용)
     */
    @Operation(summary = "Liveness 체크", description = "서비스가 살아있는지 확인합니다")
    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Map<String, Object>>> live() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ALIVE");
        status.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
