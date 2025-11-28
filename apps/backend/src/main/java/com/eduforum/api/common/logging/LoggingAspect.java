package com.eduforum.api.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * AOP 기반 로깅 - Controller 및 Service 메서드 실행 로깅
 *
 * 기능:
 * - 메서드 실행 시간 측정
 * - 입력 파라미터 및 반환값 로깅
 * - 예외 발생 시 로깅
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    /**
     * Controller 계층 포인트컷
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    /**
     * Service 계층 포인트컷
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
    }

    /**
     * Controller 메서드 실행 로깅
     */
    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.info("🎯 [Controller] {}.{}() 호출", className, methodName);

        // 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.debug("📥 [Request] {}.{}() - Parameters: {}",
                className, methodName, formatArgs(args));
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();

            stopWatch.stop();
            log.info("✅ [Controller] {}.{}() 완료 - 실행시간: {}ms",
                className, methodName, stopWatch.getTotalTimeMillis());

            // 반환값 로깅 (민감정보 제외)
            if (result != null) {
                log.debug("📤 [Response] {}.{}() - Result: {}",
                    className, methodName, formatResult(result));
            }

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("❌ [Controller] {}.{}() 실패 - 실행시간: {}ms, Error: {}",
                className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }

    /**
     * Service 메서드 실행 로깅
     */
    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.debug("🔧 [Service] {}.{}() 시작", className, methodName);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();

            stopWatch.stop();
            log.debug("✅ [Service] {}.{}() 완료 - 실행시간: {}ms",
                className, methodName, stopWatch.getTotalTimeMillis());

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("❌ [Service] {}.{}() 실패 - 실행시간: {}ms, Error: {}",
                className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }

    /**
     * 파라미터를 안전하게 포맷팅 (민감정보 마스킹)
     */
    private String formatArgs(Object[] args) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(maskSensitiveData(args[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "[로깅 실패]";
        }
    }

    /**
     * 반환값을 안전하게 포맷팅
     */
    private String formatResult(Object result) {
        try {
            String jsonResult = objectMapper.writeValueAsString(result);
            // 길이 제한 (500자)
            return jsonResult.length() > 500
                ? jsonResult.substring(0, 500) + "..."
                : jsonResult;
        } catch (Exception e) {
            return result.toString();
        }
    }

    /**
     * 민감정보 마스킹 (비밀번호, 토큰 등)
     */
    private String maskSensitiveData(Object obj) {
        if (obj == null) return "null";

        String str = obj.toString();
        // 비밀번호, 토큰 등이 포함된 경우 마스킹
        if (str.toLowerCase().contains("password") ||
            str.toLowerCase().contains("token") ||
            str.toLowerCase().contains("secret")) {
            return "[MASKED]";
        }

        return str.length() > 200 ? str.substring(0, 200) + "..." : str;
    }
}
