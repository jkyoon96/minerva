package com.eduforum.api.common.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 이메일 발송 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {

    /**
     * 수신자 이메일 주소
     */
    @NotBlank(message = "수신자 이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String to;

    /**
     * 이메일 제목
     */
    @NotBlank(message = "이메일 제목은 필수입니다")
    private String subject;

    /**
     * 이메일 본문 (HTML 또는 Plain Text)
     */
    private String body;

    /**
     * 템플릿 이름 (템플릿 사용 시)
     */
    private String templateName;

    /**
     * 템플릿 변수 (템플릿 사용 시)
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * HTML 이메일 여부
     */
    @Builder.Default
    private boolean html = true;

    /**
     * 발신자 이름 (선택사항)
     */
    private String fromName;

    /**
     * 답장 이메일 주소 (선택사항)
     */
    private String replyTo;

    /**
     * 템플릿 사용 여부 확인
     */
    public boolean isTemplateEmail() {
        return templateName != null && !templateName.isBlank();
    }
}
