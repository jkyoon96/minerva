#!/bin/bash

# EduForum API 설정 검증 스크립트

echo "=========================================="
echo "EduForum API 프로젝트 설정 검증"
echo "=========================================="
echo ""

# 색상 코드
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 파일 존재 확인 함수
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 (누락)"
        return 1
    fi
}

# 디렉토리 존재 확인 함수
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        return 0
    else
        echo -e "${RED}✗${NC} $1/ (누락)"
        return 1
    fi
}

echo "1. 빌드 파일 확인"
echo "-------------------"
check_file "build.gradle.kts"
check_file "settings.gradle.kts"
check_file "gradlew"
check_file "gradlew.bat"
check_file "gradle/wrapper/gradle-wrapper.properties"
echo ""

echo "2. 메인 소스 파일 확인"
echo "-------------------"
check_file "src/main/java/com/eduforum/api/EduforumApplication.java"
check_file "src/main/java/com/eduforum/api/config/SecurityConfig.java"
check_file "src/main/java/com/eduforum/api/config/SwaggerConfig.java"
check_file "src/main/java/com/eduforum/api/config/JpaConfig.java"
check_file "src/main/java/com/eduforum/api/config/WebConfig.java"
echo ""

echo "3. 공통 모듈 확인"
echo "-------------------"
check_file "src/main/java/com/eduforum/api/common/dto/ApiResponse.java"
check_file "src/main/java/com/eduforum/api/common/exception/ErrorCode.java"
check_file "src/main/java/com/eduforum/api/common/exception/BusinessException.java"
check_file "src/main/java/com/eduforum/api/common/exception/GlobalExceptionHandler.java"
echo ""

echo "4. 도메인 모듈 확인"
echo "-------------------"
check_file "src/main/java/com/eduforum/api/domain/health/HealthController.java"
echo ""

echo "5. 보안 모듈 확인"
echo "-------------------"
check_file "src/main/java/com/eduforum/api/security/jwt/JwtTokenProvider.java"
check_file "src/main/java/com/eduforum/api/security/jwt/JwtAuthenticationFilter.java"
echo ""

echo "6. 설정 파일 확인"
echo "-------------------"
check_file "src/main/resources/application.yml"
check_file "src/main/resources/application-dev.yml"
check_file "src/main/resources/application-staging.yml"
check_file "src/main/resources/application-prod.yml"
check_file "src/test/resources/application-test.yml"
echo ""

echo "7. 테스트 파일 확인"
echo "-------------------"
check_file "src/test/java/com/eduforum/api/EduforumApplicationTests.java"
echo ""

echo "8. Docker 파일 확인"
echo "-------------------"
check_file "Dockerfile"
check_file "docker-compose.yml"
check_file ".dockerignore"
echo ""

echo "9. 문서 파일 확인"
echo "-------------------"
check_file "README.md"
check_file "QUICKSTART.md"
check_file "PROJECT-STRUCTURE.md"
echo ""

echo "10. 기타 파일 확인"
echo "-------------------"
check_file ".gitignore"
echo ""

# Java 및 Gradle 버전 확인
echo "11. 환경 확인"
echo "-------------------"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}✓${NC} Java: $JAVA_VERSION"
else
    echo -e "${RED}✗${NC} Java가 설치되지 않았습니다"
fi

if [ -x "./gradlew" ]; then
    echo -e "${GREEN}✓${NC} Gradle Wrapper 실행 가능"
else
    echo -e "${RED}✗${NC} Gradle Wrapper 실행 권한 없음"
fi
echo ""

# 파일 개수 통계
echo "12. 프로젝트 통계"
echo "-------------------"
JAVA_FILES=$(find src -name "*.java" | wc -l)
YML_FILES=$(find src -name "*.yml" | wc -l)
TOTAL_FILES=$(find . -type f \( -name "*.java" -o -name "*.yml" -o -name "*.kts" -o -name "*.md" \) | wc -l)

echo "Java 파일: $JAVA_FILES"
echo "YAML 파일: $YML_FILES"
echo "전체 문서/설정: $TOTAL_FILES"
echo ""

echo "=========================================="
echo "검증 완료!"
echo "=========================================="
echo ""
echo -e "${YELLOW}다음 단계:${NC}"
echo "1. ./gradlew clean build"
echo "2. ./gradlew bootRun --args='--spring.profiles.active=dev'"
echo "3. http://localhost:8000/api/v1/health 접속"
echo "4. http://localhost:8000/api/docs/swagger-ui.html 접속"
echo ""
