# Quick Start - Docker Deployment

Get EduForum running with Docker in 5 minutes.

## Prerequisites

- Docker Desktop or Docker Engine (20.10+)
- Docker Compose (2.0+)
- 4GB RAM, 10GB disk space

## 1. Setup Environment

```bash
# Copy environment template
cp .env.example .env

# Generate secure secrets (Linux/macOS)
sed -i "s/your_secure_postgres_password/$(openssl rand -base64 32)/g" .env
sed -i "s/your_jwt_secret_key_minimum_32_characters_long/$(openssl rand -base64 32)/g" .env
sed -i "s/your_nextauth_secret_key_minimum_32_characters_long/$(openssl rand -base64 32)/g" .env
```

Or edit `.env` manually and replace all placeholder values.

## 2. Start Services

### Option A: Using Make (Recommended)

```bash
# Production mode
make up

# Development mode (hot reload)
make dev

# View logs
make logs
```

### Option B: Using Docker Compose

```bash
# Production mode
docker-compose up -d

# Development mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

## 3. Access Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Docs**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health

## 4. Common Commands

```bash
# View logs
make logs
# or
docker-compose logs -f

# Check status
make ps
# or
docker-compose ps

# Stop services
make down
# or
docker-compose down

# Restart
make restart
# or
docker-compose restart
```

## Troubleshooting

### Port already in use
```bash
# Check what's using the port
lsof -i :8080
# Kill the process or change ports in docker-compose.yml
```

### Services won't start
```bash
# View logs for errors
docker-compose logs

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Database connection failed
```bash
# Wait for database to be ready (may take 30-60 seconds)
docker-compose logs postgres

# Verify health
docker-compose exec postgres pg_isready -U eduforum
```

## Next Steps

- Read [DOCKER.md](./DOCKER.md) for detailed documentation
- Configure email, file storage, and other services
- Set up SSL/TLS for production
- Configure backups and monitoring

## Development Mode Features

When using `make dev` or `docker-compose.dev.yml`:

- **Hot Reload**: Code changes reflect immediately
- **Debug Ports**: Backend (5005), Frontend (9229)
- **Verbose Logs**: See SQL queries and debug info
- **Source Mounts**: Edit code directly in containers

## Production Checklist

Before deploying to production:

- [ ] Set strong passwords in `.env`
- [ ] Configure SSL/TLS (use nginx/traefik)
- [ ] Set up database backups
- [ ] Configure monitoring (Prometheus/Grafana)
- [ ] Review resource limits
- [ ] Set up log aggregation
- [ ] Configure email service
- [ ] Test disaster recovery

## Support

For detailed documentation, see:
- [DOCKER.md](./DOCKER.md) - Complete Docker guide
- [README.md](./README.md) - Project overview
- `/docs` - Technical documentation
