package com.eduforum.api.common.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP 요청/응답 로깅 필터
 *
 * 기능:
 * - 모든 HTTP 요청에 대한 로깅
 * - MDC를 사용한 요청 추적 (traceId)
 * - 요청/응답 시간 측정
 * - 요청 URL, 메서드, 상태코드 로깅
 */
@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // TraceId 생성 및 MDC 설정
        String traceId = generateTraceId();
        MDC.put(TRACE_ID, traceId);
        MDC.put(REQUEST_ID, UUID.randomUUID().toString().substring(0, 8));

        // 요청/응답 캐싱 래퍼 사용 (body 로깅을 위해)
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // 요청 정보 로깅
            logRequest(cachingRequest, traceId);

            // 필터 체인 실행
            chain.doFilter(cachingRequest, cachingResponse);

            // 응답 정보 로깅
            logResponse(cachingRequest, cachingResponse, startTime, traceId);

            // 응답 본문을 실제 응답으로 복사
            cachingResponse.copyBodyToResponse();

        } catch (Exception e) {
            log.error("❌ [TraceId: {}] 요청 처리 중 예외 발생: {}", traceId, e.getMessage(), e);
            throw e;
        } finally {
            // MDC 클리어
            MDC.clear();
        }
    }

    /**
     * 요청 정보 로깅
     */
    private void logRequest(HttpServletRequest request, String traceId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? uri + "?" + queryString : uri;
        String clientIp = getClientIp(request);

        log.info("📨 [TraceId: {}] {} {} - IP: {}",
            traceId, method, fullUrl, clientIp);

        // 헤더 로깅 (선택적)
        if (log.isDebugEnabled()) {
            log.debug("📋 [TraceId: {}] Headers - User-Agent: {}, Content-Type: {}",
                traceId,
                request.getHeader("User-Agent"),
                request.getHeader("Content-Type"));
        }
    }

    /**
     * 응답 정보 로깅
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response,
                             long startTime, String traceId) {
        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        String statusEmoji = getStatusEmoji(status);

        log.info("{} [TraceId: {}] {} {} - Status: {} - Duration: {}ms",
            statusEmoji, traceId, method, uri, status, duration);

        // 느린 요청 경고 (2초 이상)
        if (duration > 2000) {
            log.warn("⚠️ [TraceId: {}] 느린 요청 감지 - {} {} - Duration: {}ms",
                traceId, method, uri, duration);
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * TraceId 생성
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * HTTP 상태코드에 따른 이모지 반환
     */
    private String getStatusEmoji(int status) {
        if (status >= 200 && status < 300) {
            return "✅"; // 성공
        } else if (status >= 300 && status < 400) {
            return "🔄"; // 리다이렉트
        } else if (status >= 400 && status < 500) {
            return "⚠️"; // 클라이언트 오류
        } else if (status >= 500) {
            return "❌"; // 서버 오류
        }
        return "ℹ️";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("✅ RequestLoggingFilter 초기화 완료");
    }

    @Override
    public void destroy() {
        log.info("👋 RequestLoggingFilter 종료");
    }
}
