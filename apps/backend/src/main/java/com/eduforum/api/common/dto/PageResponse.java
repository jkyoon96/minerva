package com.eduforum.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 응답 래퍼
 *
 * 기능:
 * - 페이징된 데이터와 메타데이터 제공
 * - Spring Data Page를 PageResponse로 변환
 * - 페이지 정보 (현재 페이지, 전체 페이지, 전체 아이템 수 등) 포함
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이징 응답")
public class PageResponse<T> {

    /**
     * 응답 데이터 리스트
     */
    @Schema(description = "데이터 리스트")
    private List<T> content;

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int page;

    /**
     * 페이지 크기
     */
    @Schema(description = "페이지 크기", example = "20")
    private int size;

    /**
     * 전체 아이템 수
     */
    @Schema(description = "전체 아이템 수", example = "100")
    private long totalElements;

    /**
     * 전체 페이지 수
     */
    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    /**
     * 현재 페이지가 첫 페이지인지 여부
     */
    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;

    /**
     * 현재 페이지가 마지막 페이지인지 여부
     */
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    /**
     * 현재 페이지가 비어있는지 여부
     */
    @Schema(description = "빈 페이지 여부", example = "false")
    private boolean empty;

    /**
     * 정렬 필드
     */
    @Schema(description = "정렬 필드", example = "createdAt")
    private String sortField;

    /**
     * 정렬 방향
     */
    @Schema(description = "정렬 방향", example = "DESC")
    private String sortDirection;

    /**
     * Spring Data Page를 PageResponse로 변환
     *
     * @param page Spring Data Page 객체
     * @param <T>  데이터 타입
     * @return PageResponse
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .empty(page.isEmpty())
            .sortField(getSortField(page))
            .sortDirection(getSortDirection(page))
            .build();
    }

    /**
     * Spring Data Page를 PageResponse로 변환 (내용 변환 포함)
     * Entity -> DTO 변환에 유용
     *
     * @param page    Spring Data Page 객체
     * @param content 변환된 DTO 리스트
     * @param <T>     DTO 타입
     * @return PageResponse
     */
    public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
        return PageResponse.<T>builder()
            .content(content)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .empty(page.isEmpty())
            .sortField(getSortField(page))
            .sortDirection(getSortDirection(page))
            .build();
    }

    /**
     * 빈 PageResponse 생성
     *
     * @param <T> 데이터 타입
     * @return 빈 PageResponse
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
            .content(List.of())
            .page(0)
            .size(0)
            .totalElements(0)
            .totalPages(0)
            .first(true)
            .last(true)
            .empty(true)
            .build();
    }

    /**
     * 단일 페이지 응답 생성 (페이징 없이 전체 데이터 반환)
     *
     * @param content 전체 데이터 리스트
     * @param <T>     데이터 타입
     * @return PageResponse
     */
    public static <T> PageResponse<T> singlePage(List<T> content) {
        int totalElements = content.size();
        return PageResponse.<T>builder()
            .content(content)
            .page(0)
            .size(totalElements)
            .totalElements(totalElements)
            .totalPages(1)
            .first(true)
            .last(true)
            .empty(content.isEmpty())
            .build();
    }

    /**
     * Page에서 정렬 필드 추출
     */
    private static String getSortField(Page<?> page) {
        if (page.getSort().isSorted()) {
            return page.getSort().iterator().next().getProperty();
        }
        return null;
    }

    /**
     * Page에서 정렬 방향 추출
     */
    private static String getSortDirection(Page<?> page) {
        if (page.getSort().isSorted()) {
            return page.getSort().iterator().next().getDirection().name();
        }
        return null;
    }

    /**
     * 다음 페이지가 있는지 여부
     *
     * @return 다음 페이지 존재 여부
     */
    public boolean hasNext() {
        return !last;
    }

    /**
     * 이전 페이지가 있는지 여부
     *
     * @return 이전 페이지 존재 여부
     */
    public boolean hasPrevious() {
        return !first;
    }

    /**
     * 현재 페이지의 아이템 수
     *
     * @return 현재 페이지 아이템 수
     */
    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }
}
