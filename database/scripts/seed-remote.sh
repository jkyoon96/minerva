#!/bin/bash
#
# EduForum 원격 데이터베이스 시딩 스크립트
#
# 사용법: ./seed-remote.sh
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
SEEDS_DIR="$SCRIPT_DIR/../seeds"

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "=============================================="
echo "EduForum 테스트 데이터 시딩"
echo "=============================================="
echo ""

export PGPASSWORD="$DB_PASSWORD"

echo -e "${YELLOW}시딩 실행 중...${NC}"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$SEEDS_DIR/001_test_data.sql"

echo ""
echo -e "${GREEN}✓ 시딩 완료!${NC}"
echo ""

# 생성된 데이터 확인
echo "생성된 테스트 데이터:"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "
SELECT 'users' as table_name, COUNT(*) as count FROM auth.users
UNION ALL
SELECT 'roles', COUNT(*) FROM auth.roles
UNION ALL
SELECT 'courses', COUNT(*) FROM course.courses
UNION ALL
SELECT 'sessions', COUNT(*) FROM course.sessions;
"
