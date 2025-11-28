#!/bin/bash
#
# EduForum 원격 데이터베이스 설정 스크립트
#
# 사용법: ./remote-setup.sh
#
# 이 스크립트는 원격 PostgreSQL 서버에 데이터베이스와 사용자를 생성합니다.
#

set -e

# 원격 서버 설정
DB_HOST="210.115.229.12"
DB_PORT="5432"
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="sopoong"

# 생성할 데이터베이스/사용자 정보
DB_NAME="eduforum"
DB_USER="eduforum"
DB_PASSWORD="eduforum12"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=============================================="
echo "EduForum 원격 데이터베이스 설정"
echo "=============================================="
echo ""
echo "서버: $DB_HOST:$DB_PORT"
echo "데이터베이스: $DB_NAME"
echo "사용자: $DB_USER"
echo ""

# psql 설치 확인
if ! command -v psql &> /dev/null; then
    echo -e "${RED}오류: psql이 설치되어 있지 않습니다.${NC}"
    echo ""
    echo "설치 방법:"
    echo "  Ubuntu/Debian: sudo apt-get install postgresql-client"
    echo "  macOS: brew install postgresql"
    echo "  Windows: PostgreSQL 설치 또는 pgAdmin 사용"
    echo ""
    exit 1
fi

# 환경변수로 비밀번호 설정
export PGPASSWORD="$POSTGRES_PASSWORD"

echo -e "${YELLOW}[1/4] 데이터베이스 존재 여부 확인...${NC}"
DB_EXISTS=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" 2>/dev/null || echo "0")

if [ "$DB_EXISTS" = "1" ]; then
    echo -e "${YELLOW}데이터베이스 '$DB_NAME'이(가) 이미 존재합니다.${NC}"
    read -p "기존 데이터베이스를 삭제하고 다시 생성하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "기존 데이터베이스 삭제 중..."
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "
            SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '$DB_NAME';
        " > /dev/null 2>&1 || true
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;"
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "DROP USER IF EXISTS $DB_USER;"
    else
        echo "기존 데이터베이스를 유지합니다."
        echo ""
        echo -e "${GREEN}마이그레이션만 실행하려면:${NC}"
        echo "  ./migrate-remote.sh"
        exit 0
    fi
fi

echo -e "${YELLOW}[2/4] 사용자 '$DB_USER' 생성...${NC}"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "
    CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
"
echo -e "${GREEN}✓ 사용자 생성 완료${NC}"

echo -e "${YELLOW}[3/4] 데이터베이스 '$DB_NAME' 생성...${NC}"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "
    CREATE DATABASE $DB_NAME
    WITH OWNER = $DB_USER
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;
"
echo -e "${GREEN}✓ 데이터베이스 생성 완료${NC}"

echo -e "${YELLOW}[4/4] 권한 설정...${NC}"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d postgres -c "
    GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -d "$DB_NAME" -c "
    GRANT ALL ON SCHEMA public TO $DB_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
"
echo -e "${GREEN}✓ 권한 설정 완료${NC}"

echo ""
echo "=============================================="
echo -e "${GREEN}데이터베이스 설정 완료!${NC}"
echo "=============================================="
echo ""
echo "접속 정보:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "접속 테스트:"
echo "  psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
echo ""
echo "다음 단계: 마이그레이션 실행"
echo "  ./migrate-remote.sh"
echo ""
