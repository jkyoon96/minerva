package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 백업 코드 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "백업 코드 응답")
public class BackupCodesResponse {

    @Schema(description = "백업 코드 목록 (각 8자리)",
            example = "[\"12345678\", \"87654321\", \"11223344\", \"44332211\", \"55667788\", \"88776655\", \"99001122\", \"22110099\", \"33445566\", \"66554433\"]")
    private List<String> codes;

    @Schema(description = "생성된 백업 코드 개수", example = "10")
    private Integer count;

    @Schema(description = "경고 메시지",
            example = "이 백업 코드는 한 번만 표시됩니다. 안전한 곳에 보관하세요.")
    @Builder.Default
    private String warning = "이 백업 코드는 한 번만 표시됩니다. 안전한 곳에 보관하세요.";
}
