package com.eduforum.api.domain.analytics.entity;

/**
 * Risk level for at-risk students
 */
public enum RiskLevel {
    LOW,        // 낮음 (0-25%)
    MEDIUM,     // 보통 (26-50%)
    HIGH,       // 높음 (51-75%)
    CRITICAL    // 심각 (76-100%)
}
