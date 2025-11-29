package com.eduforum.api.common.email.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 이메일 템플릿 서비스
 * HTML 템플릿을 로드하고 변수를 치환합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private static final String TEMPLATE_BASE_PATH = "templates/email/";

    /**
     * 템플릿을 렌더링합니다.
     *
     * @param templateName 템플릿 이름 (확장자 제외)
     * @param variables 템플릿 변수
     * @return 렌더링된 HTML
     */
    public String renderTemplate(String templateName, Map<String, Object> variables) {
        try {
            // 템플릿 로드
            String template = loadTemplate(templateName);

            // 변수 치환
            return replaceVariables(template, variables);

        } catch (IOException e) {
            log.error("Failed to render email template: {}", templateName, e);
            throw new RuntimeException("Failed to render email template: " + templateName, e);
        }
    }

    /**
     * 템플릿의 제목을 가져옵니다.
     * 템플릿에 {{subject}} 변수가 있으면 그 값을 반환합니다.
     *
     * @param templateName 템플릿 이름
     * @param variables 템플릿 변수
     * @return 제목 (변수에 subject가 있으면 해당 값, 없으면 null)
     */
    public String getTemplateSubject(String templateName, Map<String, Object> variables) {
        if (variables.containsKey("subject")) {
            return variables.get("subject").toString();
        }

        // 템플릿별 기본 제목 반환
        return switch (templateName) {
            case "welcome" -> "EduForum에 오신 것을 환영합니다!";
            case "email-verification" -> "이메일 인증을 완료해주세요";
            case "password-reset" -> "비밀번호 재설정 요청";
            case "course-invitation" -> "코스 초대장";
            default -> null;
        };
    }

    /**
     * 템플릿 파일을 로드합니다.
     */
    private String loadTemplate(String templateName) throws IOException {
        String templatePath = TEMPLATE_BASE_PATH + templateName + ".html";
        ClassPathResource resource = new ClassPathResource(templatePath);

        if (!resource.exists()) {
            throw new IOException("Template not found: " + templatePath);
        }

        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * 템플릿의 변수를 실제 값으로 치환합니다.
     * {{variableName}} 형식의 변수를 치환합니다.
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        String result = template;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }

        return result;
    }
}
