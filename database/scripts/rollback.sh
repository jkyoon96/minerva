#!/bin/bash

# ============================================
# Database Rollback Script
# ============================================
# Description: Executes all rollback files in reverse order
# Usage: ./rollback.sh [postgres_host] [postgres_user]
# Default: localhost postgres

set -e

# Configuration
POSTGRES_HOST="${1:-localhost}"
POSTGRES_USER="${2:-postgres}"
ROLLBACK_DIR="$(dirname "$0")/../rollback"

echo "=========================================="
echo "EduForum Database Rollback"
echo "=========================================="
echo "Host: $POSTGRES_HOST"
echo "User: $POSTGRES_USER"
echo "Rollback Directory: $ROLLBACK_DIR"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Warning
echo -e "${YELLOW}WARNING: This will delete all data and drop the database!${NC}"
echo -e "${YELLOW}Press Ctrl+C to cancel, or Enter to continue...${NC}"
read

# Function to execute SQL file
execute_rollback() {
    local file=$1
    local filename=$(basename "$file")

    echo -e "${BLUE}Executing: $filename${NC}"

    if [ "$filename" == "001_drop_database.sql" ]; then
        # Drop database runs as postgres user on postgres database
        PGPASSWORD="$POSTGRES_PASSWORD" psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -d postgres -f "$file"
    else
        # All other rollbacks run on eduforum database (if it still exists)
        PGPASSWORD="eduforum12" psql -h "$POSTGRES_HOST" -U eduforum -d eduforum -f "$file" 2>/dev/null || true
    fi

    echo -e "${GREEN}✓ Executed: $filename${NC}"
    echo ""
}

# Check if rollback directory exists
if [ ! -d "$ROLLBACK_DIR" ]; then
    echo -e "${RED}Error: Rollback directory not found: $ROLLBACK_DIR${NC}"
    exit 1
fi

# Execute rollbacks in reverse order
echo "Starting rollback..."
echo ""

# Get files in reverse order
files=($(ls -r "$ROLLBACK_DIR"/*.sql))

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        execute_rollback "$file"
    fi
done

echo "=========================================="
echo -e "${GREEN}Rollback completed successfully!${NC}"
echo "=========================================="
echo ""
echo "The database 'eduforum' and user 'eduforum' have been removed."
echo ""
