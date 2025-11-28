package com.eduforum.api.common.dto;

import com.eduforum.api.common.constant.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * 페이징 요청 DTO
 *
 * 기능:
 * - 페이지 번호, 크기 지정
 * - 정렬 필드, 방향 지정
 * - Spring Data Pageable 변환
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이징 요청")
public class PageRequest {

    /**
     * 페이지 번호 (0부터 시작)
     */
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    @Builder.Default
    private int page = ApiConstants.DEFAULT_PAGE_NUMBER;

    /**
     * 페이지 크기
     */
    @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = ApiConstants.MAX_PAGE_SIZE, message = "페이지 크기는 " + ApiConstants.MAX_PAGE_SIZE + " 이하여야 합니다.")
    @Builder.Default
    private int size = ApiConstants.DEFAULT_PAGE_SIZE;

    /**
     * 정렬 필드
     */
    @Schema(description = "정렬 필드", example = "createdAt", defaultValue = "createdAt")
    @Builder.Default
    private String sortField = ApiConstants.DEFAULT_SORT_FIELD;

    /**
     * 정렬 방향 (ASC, DESC)
     */
    @Schema(description = "정렬 방향", example = "DESC", defaultValue = "DESC", allowableValues = {"ASC", "DESC"})
    @Builder.Default
    private String sortDirection = ApiConstants.DEFAULT_SORT_DIRECTION;

    /**
     * Spring Data Pageable로 변환
     *
     * @return org.springframework.data.domain.Pageable
     */
    public org.springframework.data.domain.Pageable toPageable() {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortField);

        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }

    /**
     * 정렬 없이 Pageable로 변환
     *
     * @return org.springframework.data.domain.Pageable
     */
    public org.springframework.data.domain.Pageable toPageableWithoutSort() {
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

    /**
     * 커스텀 정렬로 Pageable 생성
     *
     * @param customSort 커스텀 Sort 객체
     * @return org.springframework.data.domain.Pageable
     */
    public org.springframework.data.domain.Pageable toPageable(Sort customSort) {
        return org.springframework.data.domain.PageRequest.of(page, size, customSort);
    }

    /**
     * 다음 페이지 요청 생성
     *
     * @return 다음 페이지 PageRequest
     */
    public PageRequest nextPage() {
        return PageRequest.builder()
            .page(this.page + 1)
            .size(this.size)
            .sortField(this.sortField)
            .sortDirection(this.sortDirection)
            .build();
    }

    /**
     * 이전 페이지 요청 생성
     *
     * @return 이전 페이지 PageRequest (첫 페이지면 현재 페이지)
     */
    public PageRequest previousPage() {
        int prevPage = Math.max(0, this.page - 1);
        return PageRequest.builder()
            .page(prevPage)
            .size(this.size)
            .sortField(this.sortField)
            .sortDirection(this.sortDirection)
            .build();
    }

    /**
     * 첫 페이지 요청 생성
     *
     * @return 첫 페이지 PageRequest
     */
    public PageRequest firstPage() {
        return PageRequest.builder()
            .page(0)
            .size(this.size)
            .sortField(this.sortField)
            .sortDirection(this.sortDirection)
            .build();
    }

    /**
     * 오프셋 계산
     *
     * @return 오프셋 (시작 인덱스)
     */
    public long getOffset() {
        return (long) page * size;
    }
}
