package com.eduforum.api.domain.auth.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Login attempt entity (maps to auth.login_attempts table)
 */
@Entity
@Table(schema = "auth", name = "login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "attempt_time", nullable = false)
    private OffsetDateTime attemptTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean success = false;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;
}
