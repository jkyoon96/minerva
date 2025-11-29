package com.eduforum.api.common.email;

import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.email.dto.EmailResult;

import java.util.Map;

/**
 * 이메일 발송 서비스 인터페이스
 */
public interface EmailService {

    /**
     * 이메일 발송 (일반 텍스트/HTML)
     *
     * @param to 수신자 이메일
     * @param subject 제목
     * @param body 본문 (HTML 또는 Plain Text)
     * @return 발송 결과
     */
    EmailResult sendEmail(String to, String subject, String body);

    /**
     * 템플릿을 사용한 이메일 발송
     *
     * @param to 수신자 이메일
     * @param templateName 템플릿 이름
     * @param variables 템플릿 변수
     * @return 발송 결과
     */
    EmailResult sendTemplateEmail(String to, String templateName, Map<String, Object> variables);

    /**
     * 이메일 요청 객체를 통한 발송
     *
     * @param request 이메일 요청 정보
     * @return 발송 결과
     */
    EmailResult send(EmailRequest request);

    /**
     * 비동기 이메일 발송 (큐에 등록)
     *
     * @param request 이메일 요청 정보
     */
    void sendAsync(EmailRequest request);
}
