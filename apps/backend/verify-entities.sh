#!/bin/bash

################################################################################
# Entity Verification Script
# Verifies that all required JPA entities and repositories have been created
################################################################################

set -e

echo "=================================================="
echo "JPA Entity Verification Script"
echo "=================================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counters
TOTAL=0
PASSED=0
FAILED=0

# Function to check if file exists
check_file() {
    local file=$1
    local description=$2
    TOTAL=$((TOTAL + 1))

    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $description"
        PASSED=$((PASSED + 1))
        return 0
    else
        echo -e "${RED}✗${NC} $description"
        echo "   Missing: $file"
        FAILED=$((FAILED + 1))
        return 1
    fi
}

# Function to check if content exists in file
check_content() {
    local file=$1
    local pattern=$2
    local description=$3
    TOTAL=$((TOTAL + 1))

    if [ -f "$file" ] && grep -q "$pattern" "$file"; then
        echo -e "${GREEN}✓${NC} $description"
        PASSED=$((PASSED + 1))
        return 0
    else
        echo -e "${RED}✗${NC} $description"
        if [ ! -f "$file" ]; then
            echo "   File not found: $file"
        else
            echo "   Pattern not found: $pattern"
        fi
        FAILED=$((FAILED + 1))
        return 1
    fi
}

BASE_DIR="src/main/java/com/eduforum/api/domain"

echo "Checking Base Entity..."
echo "----------------------"
check_file "$BASE_DIR/common/entity/BaseEntity.java" "BaseEntity class"
check_content "$BASE_DIR/common/entity/BaseEntity.java" "@MappedSuperclass" "BaseEntity has @MappedSuperclass"
check_content "$BASE_DIR/common/entity/BaseEntity.java" "@CreatedDate" "BaseEntity has @CreatedDate"
check_content "$BASE_DIR/common/entity/BaseEntity.java" "deletedAt" "BaseEntity has soft delete field"
echo ""

echo "Checking Auth Schema Entities..."
echo "--------------------------------"
check_file "$BASE_DIR/auth/entity/User.java" "User entity"
check_file "$BASE_DIR/auth/entity/Role.java" "Role entity"
check_file "$BASE_DIR/auth/entity/Permission.java" "Permission entity"
check_file "$BASE_DIR/auth/entity/UserRole.java" "UserRole entity"
check_file "$BASE_DIR/auth/entity/RolePermission.java" "RolePermission entity"
check_file "$BASE_DIR/auth/entity/RefreshToken.java" "RefreshToken entity"
check_file "$BASE_DIR/auth/entity/TwoFactorAuth.java" "TwoFactorAuth entity"
check_file "$BASE_DIR/auth/entity/UserStatus.java" "UserStatus enum"
echo ""

echo "Checking Auth Entity Content..."
echo "-------------------------------"
check_content "$BASE_DIR/auth/entity/User.java" "@Table(schema = \"auth\"" "User has schema annotation"
check_content "$BASE_DIR/auth/entity/User.java" "extends BaseEntity" "User extends BaseEntity"
check_content "$BASE_DIR/auth/entity/Role.java" "@Table(schema = \"auth\"" "Role has schema annotation"
check_content "$BASE_DIR/auth/entity/RefreshToken.java" "@JdbcTypeCode" "RefreshToken has JSONB support"
echo ""

echo "Checking Auth Repositories..."
echo "-----------------------------"
check_file "$BASE_DIR/auth/repository/UserRepository.java" "UserRepository"
check_file "$BASE_DIR/auth/repository/RoleRepository.java" "RoleRepository"
check_file "$BASE_DIR/auth/repository/PermissionRepository.java" "PermissionRepository"
check_file "$BASE_DIR/auth/repository/RefreshTokenRepository.java" "RefreshTokenRepository"
echo ""

echo "Checking Repository Content..."
echo "------------------------------"
check_content "$BASE_DIR/auth/repository/UserRepository.java" "extends JpaRepository" "UserRepository extends JpaRepository"
check_content "$BASE_DIR/auth/repository/UserRepository.java" "findByEmail" "UserRepository has findByEmail"
check_content "$BASE_DIR/auth/repository/RefreshTokenRepository.java" "findValidByTokenHash" "RefreshTokenRepository has custom query"
echo ""

echo "Checking Course Schema Entities..."
echo "----------------------------------"
check_file "$BASE_DIR/course/entity/Course.java" "Course entity"
check_file "$BASE_DIR/course/entity/CourseSession.java" "CourseSession entity"
check_file "$BASE_DIR/course/entity/Enrollment.java" "Enrollment entity"
check_file "$BASE_DIR/course/entity/Assignment.java" "Assignment entity"
check_file "$BASE_DIR/course/entity/EnrollmentRole.java" "EnrollmentRole enum"
check_file "$BASE_DIR/course/entity/EnrollmentStatus.java" "EnrollmentStatus enum"
check_file "$BASE_DIR/course/entity/SessionStatus.java" "SessionStatus enum"
check_file "$BASE_DIR/course/entity/AssignmentStatus.java" "AssignmentStatus enum"
echo ""

echo "Checking Course Entity Content..."
echo "---------------------------------"
check_content "$BASE_DIR/course/entity/Course.java" "@Table(schema = \"course\"" "Course has schema annotation"
check_content "$BASE_DIR/course/entity/Course.java" "extends BaseEntity" "Course extends BaseEntity"
check_content "$BASE_DIR/course/entity/CourseSession.java" "@Table(schema = \"course\"" "CourseSession has schema annotation"
check_content "$BASE_DIR/course/entity/Course.java" "@JdbcTypeCode" "Course has JSONB support"
echo ""

echo "Checking Course Repositories..."
echo "-------------------------------"
check_file "$BASE_DIR/course/repository/CourseRepository.java" "CourseRepository"
check_file "$BASE_DIR/course/repository/CourseSessionRepository.java" "CourseSessionRepository"
check_file "$BASE_DIR/course/repository/EnrollmentRepository.java" "EnrollmentRepository"
check_file "$BASE_DIR/course/repository/AssignmentRepository.java" "AssignmentRepository"
echo ""

echo "Checking Configuration..."
echo "------------------------"
check_file "src/main/java/com/eduforum/api/config/JpaConfig.java" "JpaConfig"
check_content "src/main/java/com/eduforum/api/config/JpaConfig.java" "@EnableJpaRepositories" "JpaConfig has @EnableJpaRepositories"
check_file "src/main/resources/application-dev.yml" "application-dev.yml"
check_content "src/main/resources/application-dev.yml" "jdbc:postgresql://210.115.229.12:5432/eduforum" "Database connection configured"
check_content "src/main/resources/application-dev.yml" "ddl-auto: validate" "Hibernate validation mode set"
echo ""

echo "Checking Documentation..."
echo "------------------------"
check_file "JPA_ENTITIES_DOCUMENTATION.md" "Entity documentation"
check_file "ENTITY_QUICK_REFERENCE.md" "Quick reference guide"
check_file "ENTITY_RELATIONSHIPS.md" "Relationship diagrams"
check_file "BE-003_IMPLEMENTATION_SUMMARY.md" "Implementation summary"
echo ""

echo "=================================================="
echo "Verification Summary"
echo "=================================================="
echo -e "Total Checks: ${TOTAL}"
echo -e "${GREEN}Passed: ${PASSED}${NC}"
if [ $FAILED -gt 0 ]; then
    echo -e "${RED}Failed: ${FAILED}${NC}"
else
    echo -e "Failed: ${FAILED}"
fi
echo ""

# Calculate percentage
PERCENTAGE=$((PASSED * 100 / TOTAL))

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All checks passed! (100%)${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Start the application: ./gradlew bootRun"
    echo "2. Check Hibernate schema validation logs"
    echo "3. Test repository methods"
    echo "4. Create service layer"
    exit 0
else
    echo -e "${YELLOW}⚠ Some checks failed (${PERCENTAGE}% passed)${NC}"
    echo ""
    echo "Please review the failed checks above and fix the issues."
    exit 1
fi
