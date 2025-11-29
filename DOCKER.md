# Docker Deployment Guide - EduForum/Minerva

This guide explains how to run the EduForum application using Docker and Docker Compose.

## Prerequisites

- Docker 20.10 or higher
- Docker Compose 2.0 or higher
- At least 4GB of available RAM
- 10GB of free disk space

## Quick Start

### 1. Clone and Setup

```bash
# Clone the repository
git clone <repository-url>
cd minerva

# Copy environment file
cp .env.example .env

# Edit .env and set secure values for:
# - POSTGRES_PASSWORD
# - JWT_SECRET
# - NEXTAUTH_SECRET
nano .env
```

### 2. Production Deployment

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check service health
docker-compose ps
```

Access the application:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Backend Health: http://localhost:8080/api/actuator/health
- API Documentation: http://localhost:8080/api/swagger-ui.html

### 3. Development Mode

For development with hot reload:

```bash
# Start with development overrides
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Or use the shorthand
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

Development features:
- Hot reload for both frontend and backend
- Debug ports exposed (Backend: 5005, Frontend: 9229)
- Verbose logging enabled
- Auto-update database schema
- Source code mounted as volumes

## Service Architecture

### Services Overview

| Service | Port | Description |
|---------|------|-------------|
| postgres | 5432 | PostgreSQL 15 database |
| backend | 8080 | Spring Boot API server |
| frontend | 3000 | Next.js web application |

### Network

All services communicate through the `eduforum-network` bridge network:
- Frontend ’ Backend: `http://backend:8080/api`
- Backend ’ Database: `postgresql://postgres:5432/eduforum`

### Volumes

- `postgres_data`: Persistent database storage

## Configuration

### Environment Variables

#### Database
- `POSTGRES_PASSWORD`: PostgreSQL password (required)

#### Backend
- `SPRING_PROFILES_ACTIVE`: Spring profile (prod/dev)
- `JWT_SECRET`: Secret key for JWT tokens (min 32 characters)
- `JWT_EXPIRATION`: Token expiration in milliseconds
- `CORS_ALLOWED_ORIGINS`: Comma-separated allowed origins

#### Frontend
- `NEXT_PUBLIC_API_URL`: Public-facing API URL
- `API_URL`: Internal API URL (for server-side calls)
- `NEXTAUTH_URL`: NextAuth callback URL
- `NEXTAUTH_SECRET`: NextAuth encryption secret

### Health Checks

All services include health checks:
- **PostgreSQL**: `pg_isready` check every 10s
- **Backend**: Spring Actuator health endpoint every 30s
- **Frontend**: Custom health endpoint every 30s

## Common Commands

### Start Services
```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d backend
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend

# Last 100 lines
docker-compose logs --tail=100 backend
```

### Rebuild Services
```bash
# Rebuild all images
docker-compose build

# Rebuild specific service
docker-compose build backend

# Rebuild without cache
docker-compose build --no-cache
```

### Execute Commands
```bash
# Backend shell
docker-compose exec backend sh

# Database shell
docker-compose exec postgres psql -U eduforum -d eduforum

# Run database migrations
docker-compose exec backend java -jar app.jar --spring.profiles.active=prod db migrate
```

### Scale Services (if needed)
```bash
# Scale backend to 3 instances
docker-compose up -d --scale backend=3
```

## Database Management

### Backup Database
```bash
# Create backup
docker-compose exec postgres pg_dump -U eduforum eduforum > backup_$(date +%Y%m%d_%H%M%S).sql

# Or with gzip compression
docker-compose exec postgres pg_dump -U eduforum eduforum | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

### Restore Database
```bash
# Stop backend to prevent connections
docker-compose stop backend

# Restore from backup
docker-compose exec -T postgres psql -U eduforum -d eduforum < backup_20240101_120000.sql

# Or from gzipped backup
gunzip -c backup_20240101_120000.sql.gz | docker-compose exec -T postgres psql -U eduforum -d eduforum

# Start backend
docker-compose start backend
```

### Access Database
```bash
# Interactive psql
docker-compose exec postgres psql -U eduforum -d eduforum

# Run SQL file
docker-compose exec -T postgres psql -U eduforum -d eduforum < scripts/migration.sql
```

## Debugging

### Backend Debugging

In development mode, the backend exposes port 5005 for remote debugging:

**IntelliJ IDEA:**
1. Run ’ Edit Configurations
2. Add ’ Remote JVM Debug
3. Host: localhost, Port: 5005
4. Apply and Debug

**VS Code:**
```json
{
  "type": "java",
  "request": "attach",
  "name": "Debug Backend",
  "hostName": "localhost",
  "port": 5005
}
```

### Frontend Debugging

Development mode exposes port 9229 for Node.js debugging:

**VS Code:**
```json
{
  "type": "node",
  "request": "attach",
  "name": "Debug Frontend",
  "port": 9229,
  "restart": true,
  "sourceMaps": true
}
```

### View Container Stats
```bash
# Real-time stats
docker stats

# Service-specific
docker stats eduforum-backend
```

## Production Best Practices

### 1. Security

```bash
# Use strong passwords
POSTGRES_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
NEXTAUTH_SECRET=$(openssl rand -base64 32)

# Never commit .env file
echo ".env" >> .gitignore
```

### 2. SSL/TLS

Use a reverse proxy (nginx/traefik) for HTTPS:

```yaml
# docker-compose.prod.yml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
```

### 3. Resource Limits

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### 4. Logging

```yaml
services:
  backend:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 5. Monitoring

Consider adding:
- Prometheus for metrics
- Grafana for visualization
- ELK stack for log aggregation

## Troubleshooting

### Services Won't Start

```bash
# Check logs
docker-compose logs

# Check service status
docker-compose ps

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Database Connection Issues

```bash
# Verify database is running
docker-compose exec postgres pg_isready -U eduforum

# Check backend can reach database
docker-compose exec backend ping postgres

# View database logs
docker-compose logs postgres
```

### Frontend Can't Reach Backend

```bash
# Check backend health
curl http://localhost:8080/api/actuator/health

# Check from frontend container
docker-compose exec frontend wget -O- http://backend:8080/api/actuator/health

# Verify network
docker network inspect eduforum-network
```

### Out of Memory

```bash
# Check memory usage
docker stats

# Increase Docker memory (Docker Desktop)
# Settings ’ Resources ’ Memory ’ 8GB

# Or add memory limits to services
```

### Port Already in Use

```bash
# Check what's using the port
lsof -i :8080
netstat -tulpn | grep 8080

# Use different ports
docker-compose -f docker-compose.yml -p eduforum up -d
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Docker Build and Push

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Build images
        run: docker-compose build

      - name: Run tests
        run: docker-compose run --rm backend ./gradlew test

      - name: Push to registry
        run: |
          docker-compose push
```

## Updating

### Update Images

```bash
# Pull latest base images
docker-compose pull

# Rebuild with latest dependencies
docker-compose build --pull

# Restart services
docker-compose up -d
```

### Update Application

```bash
# Pull latest code
git pull

# Rebuild and restart
docker-compose up -d --build
```

## Cleanup

```bash
# Remove all containers and volumes
docker-compose down -v

# Remove unused images
docker image prune -a

# Remove everything (dangerous!)
docker system prune -a --volumes
```

## Support

For issues and questions:
- Check logs: `docker-compose logs`
- Review health checks: `docker-compose ps`
- Consult documentation: `/docs` directory
- GitHub Issues: [repository-url]/issues

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Next.js Docker Guide](https://nextjs.org/docs/deployment#docker-image)
