# CI/CD Quick Reference Card

One-page quick reference for the Minerva/EduForum CI/CD pipeline.

## Workflow Triggers

| Workflow | Push (main/develop) | Pull Request | Schedule | Manual | Tags |
|----------|:-------------------:|:------------:|:--------:|:------:|:----:|
| CI | ✅ | ✅ | - | - | - |
| Docker Build | ✅ (main only) | - | - | ✅ | ✅ (v*) |
| CodeQL | ✅ | ✅ | ✅ (Mon) | ✅ | - |
| Dependency Review | - | ✅ | - | - | - |

## Common Commands

```bash
# View workflow runs
gh run list --workflow=ci.yml

# Watch a running workflow
gh run watch

# Re-run a failed workflow
gh run rerun <run-id>

# Download artifacts
gh run download <run-id>

# Trigger manual workflow
gh workflow run docker-build.yml

# View workflow logs
gh run view <run-id> --log
```

## Status Badges

```markdown
[![CI](https://github.com/USER/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/USER/minerva/actions/workflows/ci.yml)
[![Docker](https://github.com/USER/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/USER/minerva/actions/workflows/docker-build.yml)
[![CodeQL](https://github.com/USER/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/USER/minerva/actions/workflows/codeql.yml)
```

## Pre-Commit Checklist

### Backend
```bash
cd apps/backend
./gradlew test           # Run tests
./gradlew build          # Build project
./gradlew dependencyCheckAnalyze  # Check dependencies
```

### Frontend
```bash
cd apps/frontend
npm run lint             # Lint code
npm run type-check       # Check types
npm test                 # Run tests
npm run build            # Build project
npm audit                # Check dependencies
```

## Docker Images

### Pull Images
```bash
# Latest from main
docker pull ghcr.io/USER/minerva/backend:latest
docker pull ghcr.io/USER/minerva/frontend:latest

# Specific version
docker pull ghcr.io/USER/minerva/backend:v1.0.0
docker pull ghcr.io/USER/minerva/frontend:v1.0.0
```

### Run Containers
```bash
# Backend
docker run -p 8080:8080 ghcr.io/USER/minerva/backend:latest

# Frontend
docker run -p 3000:3000 ghcr.io/USER/minerva/frontend:latest
```

## Release Process

```bash
# 1. Ensure main is clean
git checkout main
git pull origin main

# 2. Create and push tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 3. Docker images automatically built with tags:
#    - v1.0.0, v1.0, v1, latest
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| CI not running | Check path filters in workflow |
| Tests failing | Check PostgreSQL connection in logs |
| Docker build fails | Verify Dockerfile exists and is valid |
| CodeQL timeout | Exclude test files or increase timeout |
| Dependency alerts | Update dependencies or add suppression |

## File Locations

| File | Purpose |
|------|---------|
| `.github/workflows/ci.yml` | Main CI pipeline |
| `.github/workflows/docker-build.yml` | Docker builds |
| `.github/workflows/codeql.yml` | Security analysis |
| `.github/workflows/dependency-review.yml` | Dependency scanning |
| `suppression.xml` | OWASP suppressions |

## Key Settings

### Gradle Cache Key
```yaml
key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
```

### npm Cache Key
```yaml
cache-dependency-path: apps/frontend/package-lock.json
```

### Docker Platforms
```yaml
platforms: linux/amd64,linux/arm64
```

## Environment Variables

### CI Workflow
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/eduforum_test
SPRING_DATASOURCE_USERNAME: eduforum
SPRING_DATASOURCE_PASSWORD: eduforum_test_password
SPRING_PROFILES_ACTIVE: test
NODE_ENV: production
```

### Docker Build
```yaml
NEXT_PUBLIC_API_URL: ${{ vars.NEXT_PUBLIC_API_URL }}
```

## Permissions

### Workflows Use
```yaml
contents: read          # All workflows
packages: write         # Docker build only
security-events: write  # CodeQL, dependency-review
pull-requests: write    # Dependency-review only
```

## Artifacts

| Artifact | Retention | Workflow |
|----------|-----------|----------|
| Backend build | 7 days | CI |
| Frontend build | 7 days | CI |
| Test reports | 14 days | CI |
| Test results | 14 days | CI |
| Coverage | 14 days | CI |
| CodeQL results | 14 days | CodeQL |
| Dependency reports | 14 days | Dependency Review |

## Documentation

| Document | Purpose |
|----------|---------|
| `workflows/README.md` | Workflow documentation |
| `CICD_GUIDE.md` | Developer guide |
| `SETUP_CHECKLIST.md` | Setup guide |
| `STATUS_BADGES.md` | Badge templates |
| `IMPLEMENTATION_REPORT.md` | Implementation details |
| `QUICK_REFERENCE.md` | This document |

## Links

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [CodeQL Docs](https://codeql.github.com/docs/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [Docker Build Action](https://github.com/docker/build-push-action)

---

**Last Updated:** 2025-11-29
**Version:** 1.0.0
