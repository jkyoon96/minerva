#!/bin/bash

# ============================================
# Database Seed Script
# ============================================
# Description: Executes all seed data files
# Usage: ./seed.sh [postgres_host]
# Default: localhost

set -e

# Configuration
POSTGRES_HOST="${1:-localhost}"
SEED_DIR="$(dirname "$0")/../seeds"

echo "=========================================="
echo "EduForum Database Seeding"
echo "=========================================="
echo "Host: $POSTGRES_HOST"
echo "Seed Directory: $SEED_DIR"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to execute SQL file
execute_seed() {
    local file=$1
    local filename=$(basename "$file")

    echo -e "${BLUE}Executing: $filename${NC}"

    PGPASSWORD="eduforum12" psql -h "$POSTGRES_HOST" -U eduforum -d eduforum -f "$file"

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Success: $filename${NC}"
    else
        echo -e "${RED}✗ Failed: $filename${NC}"
        exit 1
    fi
    echo ""
}

# Check if seed directory exists
if [ ! -d "$SEED_DIR" ]; then
    echo -e "${RED}Error: Seed directory not found: $SEED_DIR${NC}"
    exit 1
fi

# Execute seed files in order
echo "Starting seeding..."
echo ""

for file in "$SEED_DIR"/*.sql; do
    if [ -f "$file" ]; then
        execute_seed "$file"
    fi
done

echo "=========================================="
echo -e "${GREEN}Seeding completed successfully!${NC}"
echo "=========================================="
echo ""
echo "Test users created:"
echo "  Admin:      admin@eduforum.com"
echo "  Professor:  prof.kim@eduforum.com, prof.lee@eduforum.com"
echo "  TA:         ta.park@eduforum.com"
echo "  Students:   student1-5@eduforum.com"
echo ""
echo "Password for all test users: Use the hash in the seed file"
echo ""
echo "Test courses created:"
echo "  CS101 - Introduction to Computer Science"
echo "  CS201 - Data Structures and Algorithms"
echo ""
