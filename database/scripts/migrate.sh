#!/bin/bash

# ============================================
# Database Migration Script
# ============================================
# Description: Executes all migration files in order
# Usage: ./migrate.sh [postgres_host] [postgres_user]
# Default: localhost postgres

set -e

# Configuration
POSTGRES_HOST="${1:-localhost}"
POSTGRES_USER="${2:-postgres}"
MIGRATION_DIR="$(dirname "$0")/../migrations"

echo "=========================================="
echo "EduForum Database Migration"
echo "=========================================="
echo "Host: $POSTGRES_HOST"
echo "User: $POSTGRES_USER"
echo "Migration Directory: $MIGRATION_DIR"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to execute SQL file
execute_migration() {
    local file=$1
    local filename=$(basename "$file")

    echo -e "${BLUE}Executing: $filename${NC}"

    if [ "$filename" == "001_create_database.sql" ]; then
        # First migration creates database, run as postgres user on postgres database
        PGPASSWORD="$POSTGRES_PASSWORD" psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -d postgres -f "$file"
    else
        # All other migrations run on eduforum database
        PGPASSWORD="eduforum12" psql -h "$POSTGRES_HOST" -U eduforum -d eduforum -f "$file"
    fi

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Success: $filename${NC}"
    else
        echo -e "${RED}✗ Failed: $filename${NC}"
        exit 1
    fi
    echo ""
}

# Check if migration directory exists
if [ ! -d "$MIGRATION_DIR" ]; then
    echo -e "${RED}Error: Migration directory not found: $MIGRATION_DIR${NC}"
    exit 1
fi

# Execute migrations in order
echo "Starting migration..."
echo ""

for file in "$MIGRATION_DIR"/*.sql; do
    if [ -f "$file" ]; then
        execute_migration "$file"
    fi
done

echo "=========================================="
echo -e "${GREEN}Migration completed successfully!${NC}"
echo "=========================================="
echo ""
echo "Database: eduforum"
echo "User: eduforum"
echo "Password: eduforum12"
echo ""
echo "Next steps:"
echo "  1. Run seed script: ./seed.sh"
echo "  2. Verify tables: psql -h $POSTGRES_HOST -U eduforum -d eduforum -c '\dt auth.*'"
echo ""
