#!/bin/bash
#
# EduForum 원격 데이터베이스 마이그레이션 스크립트
#
# 사용법: ./migrate-remote.sh
#

set -e

# 원격 서버 설정
DB_HOST="210.115.229.12"
DB_PORT="5432"
DB_NAME="eduforum"
DB_USER="eduforum"
DB_PASSWORD="eduforum12"

# 스크립트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MIGRATIONS_DIR="$SCRIPT_DIR/../migrations"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "=============================================="
echo "EduForum 원격 데이터베이스 마이그레이션"
echo "=============================================="
echo ""
echo "서버: $DB_HOST:$DB_PORT"
echo "데이터베이스: $DB_NAME"
echo ""

# psql 설치 확인
if ! command -v psql &> /dev/null; then
    echo -e "${RED}오류: psql이 설치되어 있지 않습니다.${NC}"
    exit 1
fi

# 환경변수로 비밀번호 설정
export PGPASSWORD="$DB_PASSWORD"

# 연결 테스트
echo -e "${YELLOW}연결 테스트 중...${NC}"
if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1;" > /dev/null 2>&1; then
    echo -e "${RED}데이터베이스 연결 실패!${NC}"
    echo "먼저 remote-setup.sh를 실행하세요."
    exit 1
fi
echo -e "${GREEN}✓ 연결 성공${NC}"
echo ""

# 마이그레이션 파일 실행 (002번부터 - 001은 DB 생성이므로 제외)
MIGRATION_FILES=(
    "002_create_extensions_and_types.sql"
    "003_auth_schema.sql"
    "004_course_schema.sql"
    "005_live_schema.sql"
    "006_learning_schema.sql"
    "007_assess_schema.sql"
    "008_analytics_schema.sql"
    "009_indexes.sql"
    "010_initial_data.sql"
)

echo "마이그레이션 실행 중..."
echo ""

for file in "${MIGRATION_FILES[@]}"; do
    filepath="$MIGRATIONS_DIR/$file"
    if [ -f "$filepath" ]; then
        echo -e "${YELLOW}[실행] $file${NC}"
        if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$filepath" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 완료${NC}"
        else
            echo -e "${RED}✗ 실패${NC}"
            echo "오류 발생. 로그를 확인하세요."
            # 상세 오류 출력
            psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$filepath" 2>&1 | tail -20
            exit 1
        fi
    else
        echo -e "${RED}파일 없음: $file${NC}"
    fi
done

echo ""
echo "=============================================="
echo -e "${GREEN}마이그레이션 완료!${NC}"
echo "=============================================="
echo ""

# 테이블 확인
echo "생성된 스키마 및 테이블:"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "
SELECT schemaname, COUNT(*) as tables
FROM pg_tables
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY schemaname;
"

echo ""
echo "시딩을 실행하려면:"
echo "  ./seed-remote.sh"
echo ""
