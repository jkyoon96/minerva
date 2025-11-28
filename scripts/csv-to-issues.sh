#!/bin/bash
# CSV를 GitHub Issues로 변환하는 스크립트
# Usage: ./scripts/csv-to-issues.sh <csv-file> [--epic E1] [--priority P0] [--dry-run]

set -e

CSV_FILE="${1:-docs/09-git-issues-tasks.csv}"
MAPPING_FILE="scripts/issues-mapping.json"
EPIC_FILTER=""
PRIORITY_FILTER=""
DRY_RUN=false
DELAY=2  # API Rate Limit 방지 (초)

# 인자 파싱
while [[ $# -gt 1 ]]; do
    case $2 in
        --epic)
            EPIC_FILTER="$3"
            shift 2
            ;;
        --priority)
            PRIORITY_FILTER="$3"
            shift 2
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

echo "=== CSV to GitHub Issues 변환 스크립트 ==="
echo "CSV File: $CSV_FILE"
echo "Epic Filter: ${EPIC_FILTER:-전체}"
echo "Priority Filter: ${PRIORITY_FILTER:-전체}"
echo "Dry Run: $DRY_RUN"
echo ""

# 인증 확인
if ! gh auth status &>/dev/null; then
    echo "Error: GitHub CLI 인증이 필요합니다."
    echo "실행: gh auth login"
    exit 1
fi

# CSV 파일 존재 확인
if [ ! -f "$CSV_FILE" ]; then
    echo "Error: CSV 파일을 찾을 수 없습니다: $CSV_FILE"
    exit 1
fi

# 매핑 파일 초기화
if [ ! -f "$MAPPING_FILE" ]; then
    echo "{}" > "$MAPPING_FILE"
fi

# Story Points → Size Label 매핑
get_size_label() {
    case $1 in
        1) echo "size:xs" ;;
        2) echo "size:s" ;;
        3) echo "size:m" ;;
        5) echo "size:l" ;;
        8) echo "size:xl" ;;
        *) echo "size:s" ;;
    esac
}

# Priority → Milestone 매핑
get_milestone() {
    case $1 in
        P0) echo "MVP (v0.1)" ;;
        P1) echo "v1.0" ;;
        P2) echo "v2.0" ;;
        *) echo "" ;;
    esac
}

# Epic → Label 매핑
get_epic_label() {
    case $1 in
        E1) echo "epic:e1-auth" ;;
        E2) echo "epic:e2-course" ;;
        E3) echo "epic:e3-live" ;;
        E4) echo "epic:e4-active" ;;
        E5) echo "epic:e5-assessment" ;;
        E6) echo "epic:e6-analytics" ;;
        *) echo "" ;;
    esac
}

# Type Label 추출
get_type_label() {
    local labels="$1"
    if [[ "$labels" == *"[DB]"* ]]; then echo "type:db"
    elif [[ "$labels" == *"[BE]"* ]]; then echo "type:be"
    elif [[ "$labels" == *"[FE]"* ]]; then echo "type:fe"
    elif [[ "$labels" == *"[DOC]"* ]]; then echo "type:doc"
    elif [[ "$labels" == *"[INFRA]"* ]]; then echo "type:infra"
    else echo ""
    fi
}

# Priority Label 매핑
get_priority_label() {
    case $1 in
        P0) echo "priority:p0-mvp" ;;
        P1) echo "priority:p1-v1" ;;
        P2) echo "priority:p2-v2" ;;
        *) echo "" ;;
    esac
}

# Dependencies를 Issue 링크로 변환
resolve_dependencies() {
    local deps="$1"
    local result=""

    if [ -z "$deps" ]; then
        echo "- 없음"
        return
    fi

    IFS=';' read -ra DEP_ARRAY <<< "$deps"
    for dep in "${DEP_ARRAY[@]}"; do
        dep=$(echo "$dep" | xargs)  # trim
        if [ -n "$dep" ]; then
            # 매핑 파일에서 Issue 번호 조회
            issue_num=$(jq -r --arg key "$dep" '.[$key] // empty' "$MAPPING_FILE")
            if [ -n "$issue_num" ]; then
                result+="- [ ] #$issue_num ($dep)\n"
            else
                result+="- [ ] $dep (미생성)\n"
            fi
        fi
    done

    if [ -z "$result" ]; then
        echo "- 없음"
    else
        echo -e "$result"
    fi
}

# Acceptance Criteria 생성
generate_acceptance_criteria() {
    local type_label="$1"
    local description="$2"

    case $type_label in
        "type:db")
            cat << 'CRITERIA'
### Database 요구사항
- [ ] 스키마 변경사항이 마이그레이션 파일로 작성됨
- [ ] 롤백 마이그레이션이 포함됨
- [ ] 인덱스 및 제약조건이 적절히 설정됨
- [ ] 테스트 데이터 시딩 스크립트 포함

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 문서화 완료
CRITERIA
            ;;
        "type:be")
            cat << 'CRITERIA'
### Backend 요구사항
- [ ] API 엔드포인트가 명세대로 구현됨
- [ ] 입력값 유효성 검사 구현
- [ ] 에러 핸들링 및 적절한 HTTP 상태 코드 반환
- [ ] 단위 테스트 작성 (커버리지 80% 이상)
- [ ] API 문서 (Swagger/OpenAPI) 업데이트

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 보안 검토 완료
CRITERIA
            ;;
        "type:fe")
            cat << 'CRITERIA'
### Frontend 요구사항
- [ ] 와이어프레임 디자인대로 UI 구현
- [ ] 반응형 디자인 적용 (모바일/태블릿/데스크톱)
- [ ] 접근성 가이드라인 준수 (WCAG 2.1 AA)
- [ ] 로딩/에러 상태 처리

### 테스트 요구사항
- [ ] 컴포넌트 단위 테스트 작성
- [ ] 스토리북 스토리 추가

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 크로스 브라우저 테스트
CRITERIA
            ;;
        "type:doc")
            cat << 'CRITERIA'
### Documentation 요구사항
- [ ] 문서 초안 작성
- [ ] 기술 검토 완료
- [ ] 스크린샷/다이어그램 포함 (필요시)

### 품질 요구사항
- [ ] 문서 리뷰 완료
- [ ] 오타/문법 검토
CRITERIA
            ;;
        *)
            cat << 'CRITERIA'
### 기본 요구사항
- [ ] 기능 구현 완료
- [ ] 테스트 작성
- [ ] 코드 리뷰 완료
CRITERIA
            ;;
    esac
}

# Story 이름 매핑
get_story_name() {
    local story_id="$1"
    case $story_id in
        E1-S1) echo "회원가입" ;;
        E1-S2) echo "로그인" ;;
        E1-S3) echo "소셜 로그인" ;;
        E1-S4) echo "2단계 인증" ;;
        E1-S5) echo "비밀번호 재설정" ;;
        E1-S6) echo "역할 기반 접근 제어" ;;
        E1-S7) echo "프로필 관리" ;;
        E2-S0) echo "대시보드" ;;
        E2-S1) echo "코스 생성/수정" ;;
        E2-S2) echo "수강생 관리" ;;
        E2-S3) echo "세션 관리" ;;
        E2-S4) echo "과제 관리" ;;
        E2-S5) echo "콘텐츠 라이브러리" ;;
        E2-S6) echo "성적 관리" ;;
        E3-S1) echo "세션 시작/참여" ;;
        E3-S2) echo "화상 기능" ;;
        E3-S3) echo "화면 공유" ;;
        E3-S4) echo "채팅" ;;
        E3-S5) echo "녹화/재생" ;;
        E3-S6) echo "레이아웃 관리" ;;
        E4-S1) echo "투표/설문" ;;
        E4-S2) echo "퀴즈" ;;
        E4-S3) echo "분반 토론" ;;
        E4-S4) echo "화이트보드" ;;
        E4-S5) echo "토론/지명" ;;
        E5-S1) echo "퀴즈 결과" ;;
        E5-S2) echo "AI 채점" ;;
        E5-S3) echo "코드 평가" ;;
        E5-S4) echo "동료 평가" ;;
        E5-S5) echo "참여도 분석" ;;
        E6-S1) echo "실시간 분석" ;;
        E6-S2) echo "리포트" ;;
        E6-S3) echo "조기 경보" ;;
        E6-S4) echo "네트워크 분석" ;;
        *) echo "$story_id" ;;
    esac
}

# Epic 이름 매핑
get_epic_name() {
    case $1 in
        E1) echo "E1: 사용자 인증" ;;
        E2) echo "E2: 코스 관리" ;;
        E3) echo "E3: 실시간 세미나" ;;
        E4) echo "E4: 액티브 러닝" ;;
        E5) echo "E5: 평가/피드백" ;;
        E6) echo "E6: 학습 분석" ;;
        *) echo "$1" ;;
    esac
}

# Issue 생성 함수
create_issue() {
    local task_id="$1"
    local epic="$2"
    local story="$3"
    local title="$4"
    local labels="$5"
    local description="$6"
    local sp="$7"
    local priority="$8"
    local deps="$9"
    local ref_docs="${10}"
    local wireframes="${11}"

    # 이미 생성된 Issue 확인
    existing=$(jq -r --arg key "$task_id" '.[$key] // empty' "$MAPPING_FILE")
    if [ -n "$existing" ]; then
        echo "[SKIP] Already exists: $task_id -> #$existing"
        return
    fi

    # Labels 구성
    local issue_labels=""
    local epic_label=$(get_epic_label "$epic")
    local type_label=$(get_type_label "$labels")
    local size_label=$(get_size_label "$sp")
    local priority_label=$(get_priority_label "$priority")

    [ -n "$epic_label" ] && issue_labels+="$epic_label,"
    [ -n "$type_label" ] && issue_labels+="$type_label,"
    [ -n "$size_label" ] && issue_labels+="$size_label,"
    [ -n "$priority_label" ] && issue_labels+="$priority_label,"

    # 의존성이 있으면 blocked 상태
    if [ -n "$deps" ]; then
        issue_labels+="status:blocked,"
    else
        issue_labels+="status:ready,"
    fi

    issue_labels=${issue_labels%,}  # 마지막 쉼표 제거

    # Milestone
    local milestone=$(get_milestone "$priority")

    # Epic/Story 이름
    local epic_name=$(get_epic_name "$epic")
    local story_name=$(get_story_name "$story")

    # Dependencies 해석
    local resolved_deps=$(resolve_dependencies "$deps")

    # Acceptance Criteria
    local acceptance=$(generate_acceptance_criteria "$type_label" "$description")

    # Wireframe 섹션
    local wireframe_section=""
    if [ -n "$wireframes" ]; then
        wireframe_section="## Wireframe Files

\`\`\`
docs/wireframes/$wireframes
\`\`\`
"
    fi

    # Reference Docs 섹션
    local ref_section=""
    if [ -n "$ref_docs" ]; then
        ref_section="## Reference Documents

"
        IFS=';' read -ra DOC_ARRAY <<< "$ref_docs"
        for doc in "${DOC_ARRAY[@]}"; do
            doc=$(echo "$doc" | xargs)
            [ -n "$doc" ] && ref_section+="- \`docs/$doc\`
"
        done
    fi

    # Issue Body 생성
    local body=$(cat << BODY
## Task 개요

| 항목 | 값 |
|------|-----|
| **Task ID** | $task_id |
| **Epic** | $epic_name |
| **Story** | $story_name |
| **Story Points** | $sp |
| **Priority** | $priority |

## Description

$description

## Dependencies

### Blocked By (선행 작업)
$resolved_deps

$ref_section
$wireframe_section
## Acceptance Criteria

$acceptance

---
> Generated from: docs/09-git-issues-tasks.csv
BODY
)

    local issue_title="[$task_id] $title"

    if [ "$DRY_RUN" = true ]; then
        echo "[DRY-RUN] Would create: $issue_title"
        echo "  Labels: $issue_labels"
        echo "  Milestone: $milestone"
        echo ""
    else
        # Issue 생성
        local issue_url
        if [ -n "$milestone" ]; then
            issue_url=$(gh issue create \
                --title "$issue_title" \
                --body "$body" \
                --label "$issue_labels" \
                --milestone "$milestone" 2>/dev/null || echo "")
        else
            issue_url=$(gh issue create \
                --title "$issue_title" \
                --body "$body" \
                --label "$issue_labels" 2>/dev/null || echo "")
        fi

        if [ -n "$issue_url" ]; then
            # Issue 번호 추출
            local issue_num=$(echo "$issue_url" | grep -oE '[0-9]+$')

            # 매핑 저장
            jq --arg key "$task_id" --arg val "$issue_num" '. + {($key): $val}' "$MAPPING_FILE" > "${MAPPING_FILE}.tmp"
            mv "${MAPPING_FILE}.tmp" "$MAPPING_FILE"

            echo "[OK] Created: $issue_title -> #$issue_num"
        else
            echo "[ERROR] Failed to create: $issue_title"
        fi

        # Rate limit 방지
        sleep $DELAY
    fi
}

# CSV 파싱 및 Issue 생성
echo "=== Issue 생성 시작 ==="
echo ""

# 첫 줄(헤더) 건너뛰고 처리
tail -n +2 "$CSV_FILE" | while IFS=',' read -r task_id epic story title labels description sp priority deps ref_docs wireframes; do
    # 필터 적용
    if [ -n "$EPIC_FILTER" ] && [ "$epic" != "$EPIC_FILTER" ]; then
        continue
    fi

    if [ -n "$PRIORITY_FILTER" ] && [ "$priority" != "$PRIORITY_FILTER" ]; then
        continue
    fi

    # 따옴표 제거
    task_id=$(echo "$task_id" | tr -d '"')
    title=$(echo "$title" | tr -d '"')
    description=$(echo "$description" | tr -d '"')
    labels=$(echo "$labels" | tr -d '"')
    wireframes=$(echo "$wireframes" | tr -d '"')

    create_issue "$task_id" "$epic" "$story" "$title" "$labels" "$description" "$sp" "$priority" "$deps" "$ref_docs" "$wireframes"
done

echo ""
echo "=== 완료 ==="
echo "매핑 파일: $MAPPING_FILE"
