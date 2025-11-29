.PHONY: help build up down logs clean restart ps db-backup db-restore dev prod

# Default target
help:
	@echo "EduForum Docker Management"
	@echo ""
	@echo "Usage:"
	@echo "  make <target>"
	@echo ""
	@echo "Targets:"
	@echo "  help        Show this help message"
	@echo "  build       Build all Docker images"
	@echo "  up          Start all services (production mode)"
	@echo "  down        Stop all services"
	@echo "  restart     Restart all services"
	@echo "  logs        View logs from all services"
	@echo "  ps          Show service status"
	@echo "  clean       Remove all containers, volumes, and images"
	@echo ""
	@echo "  dev         Start in development mode (with hot reload)"
	@echo "  prod        Start in production mode"
	@echo ""
	@echo "  db-backup   Backup database to file"
	@echo "  db-restore  Restore database from file (set FILE=backup.sql)"
	@echo ""

# Build all images
build:
	docker-compose build

# Start production mode
up prod:
	docker-compose up -d

# Start development mode
dev:
	docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Stop all services
down:
	docker-compose down

# Restart services
restart:
	docker-compose restart

# View logs
logs:
	docker-compose logs -f

# Show service status
ps:
	docker-compose ps

# Database backup
db-backup:
	@mkdir -p backups
	docker-compose exec postgres pg_dump -U eduforum eduforum > backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup created in backups/ directory"

# Database restore (use: make db-restore FILE=backups/backup_20240101_120000.sql)
db-restore:
	@if [ -z "$(FILE)" ]; then \
		echo "Error: FILE parameter required. Usage: make db-restore FILE=backups/backup.sql"; \
		exit 1; \
	fi
	docker-compose stop backend
	docker-compose exec -T postgres psql -U eduforum -d eduforum < $(FILE)
	docker-compose start backend
	@echo "Database restored from $(FILE)"

# Clean everything
clean:
	docker-compose down -v
	docker system prune -f
	@echo "Cleaned all containers, volumes, and dangling images"

# Rebuild without cache
rebuild:
	docker-compose build --no-cache

# Show resource usage
stats:
	docker stats

# Execute backend shell
backend-shell:
	docker-compose exec backend sh

# Execute frontend shell
frontend-shell:
	docker-compose exec frontend sh

# Execute database shell
db-shell:
	docker-compose exec postgres psql -U eduforum -d eduforum

# Run backend tests
test-backend:
	docker-compose exec backend ./gradlew test

# Run frontend tests
test-frontend:
	docker-compose exec frontend npm test
