package com.eduforum.api.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 현재 로그인한 사용자 정보 제공
 *
 * @CreatedBy, @LastModifiedBy 필드에 자동으로 현재 사용자 ID 설정
 */
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * 현재 인증된 사용자의 ID를 반환
     *
     * @return 사용자 ID (Optional)
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("인증되지 않은 사용자 - Auditor: SYSTEM");
                return Optional.of("SYSTEM");
            }

            Object principal = authentication.getPrincipal();

            // UserDetails 구현체인 경우
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                log.debug("현재 Auditor: {}", username);
                return Optional.of(username);
            }

            // String (username)인 경우
            if (principal instanceof String) {
                String username = (String) principal;
                log.debug("현재 Auditor: {}", username);
                return Optional.of(username);
            }

            // anonymousUser 등의 경우
            if ("anonymousUser".equals(principal)) {
                log.debug("익명 사용자 - Auditor: ANONYMOUS");
                return Optional.of("ANONYMOUS");
            }

            // 기타 경우
            log.debug("알 수 없는 principal 타입: {} - Auditor: SYSTEM", principal.getClass().getName());
            return Optional.of("SYSTEM");

        } catch (Exception e) {
            log.error("Auditor 조회 중 오류 발생 - Auditor: SYSTEM", e);
            return Optional.of("SYSTEM");
        }
    }
}
