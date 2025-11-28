#!/bin/bash
# GitHub Label 생성 스크립트
# Usage: ./scripts/create-labels.sh

set -e

echo "=== GitHub Labels 생성 스크립트 ==="
echo ""

# 인증 확인
if ! gh auth status &>/dev/null; then
    echo "Error: GitHub CLI 인증이 필요합니다."
    echo "실행: gh auth login"
    exit 1
fi

REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner 2>/dev/null)
if [ -z "$REPO" ]; then
    echo "Error: GitHub 저장소를 찾을 수 없습니다."
    exit 1
fi

echo "Repository: $REPO"
echo ""

# Label 생성 함수
create_label() {
    local name="$1"
    local color="$2"
    local description="$3"

    if gh label view "$name" &>/dev/null; then
        echo "[SKIP] Label exists: $name"
    else
        gh label create "$name" --color "$color" --description "$description"
        echo "[OK] Created: $name"
    fi
}

echo "--- Epic Labels ---"
create_label "epic:e1-auth" "7057ff" "Epic 1: 사용자 인증/인가"
create_label "epic:e2-course" "008672" "Epic 2: 코스 관리"
create_label "epic:e3-live" "d73a4a" "Epic 3: 실시간 세미나"
create_label "epic:e4-active" "0075ca" "Epic 4: 액티브 러닝 도구"
create_label "epic:e5-assessment" "e99695" "Epic 5: 평가 및 피드백"
create_label "epic:e6-analytics" "5319e7" "Epic 6: 학습 분석"

echo ""
echo "--- Type Labels ---"
create_label "type:db" "1d76db" "Database 스키마/마이그레이션"
create_label "type:be" "0e8a16" "Backend API/로직"
create_label "type:fe" "fbca04" "Frontend UI/컴포넌트"
create_label "type:doc" "c5def5" "Documentation"
create_label "type:infra" "b60205" "Infrastructure/DevOps"

echo ""
echo "--- Priority Labels ---"
create_label "priority:p0-mvp" "d93f0b" "MVP 필수 기능"
create_label "priority:p1-v1" "fbca04" "v1.0 릴리즈"
create_label "priority:p2-v2" "0e8a16" "v2.0+ 확장 기능"

echo ""
echo "--- Size Labels ---"
create_label "size:xs" "ededed" "1 SP (2-4시간)"
create_label "size:s" "c2e0c6" "2 SP (0.5-1일)"
create_label "size:m" "bfd4f2" "3 SP (1-2일)"
create_label "size:l" "d4c5f9" "5 SP (3-5일)"
create_label "size:xl" "f9d0c4" "8 SP (1-2주)"

echo ""
echo "--- Status Labels ---"
create_label "status:blocked" "b60205" "의존성으로 블록됨"
create_label "status:ready" "0e8a16" "작업 가능"
create_label "status:in-progress" "fbca04" "작업 중"
create_label "status:in-review" "1d76db" "코드 리뷰 중"

echo ""
echo "=== 완료: GitHub Labels 생성 ==="
