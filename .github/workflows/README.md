# GitHub Actions Workflows

This directory contains CI/CD workflows for the Minerva/EduForum project.

## Workflows Overview

### 1. CI Pipeline (`ci.yml`)
**Triggers:** Push to `main`/`develop` branches, Pull Requests

**Jobs:**
- **Path Detection**: Uses `dorny/paths-filter` to detect changes in backend/frontend
- **Backend Build**: Gradle build with Java 17 (Temurin)
- **Backend Test**: Unit tests with PostgreSQL test database
- **Frontend Build**: Next.js build with linting and type checking
- **Frontend Test**: Jest/Vitest tests with coverage

**Features:**
- Intelligent path filtering (only runs relevant jobs)
- Gradle/npm dependency caching
- Test report artifacts (14 days retention)
- Build artifacts (7 days retention)
- Concurrency control (cancels outdated runs)

**Status Badges:**
```markdown
![CI](https://github.com/YOUR_USERNAME/minerva/workflows/CI/badge.svg)
```

---

### 2. Docker Build (`docker-build.yml`)
**Triggers:** Push to `main` branch, version tags (`v*`), manual dispatch

**Jobs:**
- **Build Backend**: Multi-platform Docker image (linux/amd64, linux/arm64)
- **Build Frontend**: Multi-platform Docker image (linux/amd64, linux/arm64)

**Image Registry:** GitHub Container Registry (`ghcr.io`)

**Image Tags:**
- `latest` - Latest main branch build
- `main-<sha>` - Specific commit on main branch
- `v1.0.0`, `v1.0`, `v1` - Semantic version tags

**Features:**
- Multi-platform builds (AMD64, ARM64)
- GitHub Actions cache for faster builds
- Metadata extraction with docker/metadata-action
- Automatic tagging based on Git refs

**Status Badges:**
```markdown
![Docker Build](https://github.com/YOUR_USERNAME/minerva/workflows/Docker%20Build/badge.svg)
```

---

### 3. CodeQL Security Analysis (`codeql.yml`)
**Triggers:** Push to `main`/`develop`, Pull Requests, Weekly schedule (Monday 00:00 UTC), manual dispatch

**Languages:**
- Java/Kotlin (backend)
- JavaScript/TypeScript (frontend)

**Query Suites:**
- `security-extended` - Extended security queries
- `security-and-quality` - Security and code quality queries

**Features:**
- Automated vulnerability scanning
- SARIF results uploaded to GitHub Security tab
- Separate analysis for each language
- Results artifacts (14 days retention)

**Status Badges:**
```markdown
![CodeQL](https://github.com/YOUR_USERNAME/minerva/workflows/CodeQL%20Security%20Analysis/badge.svg)
```

---

### 4. Dependency Review (`dependency-review.yml`)
**Triggers:** Pull Requests to `main`/`develop`

**Jobs:**
1. **GitHub Dependency Review**
   - Fails on moderate+ severity vulnerabilities
   - Denies GPL-2.0, GPL-3.0, AGPL-3.0 licenses
   - Comments summary in PR

2. **Backend: Gradle Dependency Check**
   - OWASP Dependency Check plugin
   - HTML report artifact

3. **Frontend: npm audit**
   - npm audit with moderate level threshold
   - JSON report artifact

4. **OWASP Dependency Check**
   - Full project scan
   - SARIF uploaded to GitHub Security
   - Fails on CVSS 7+ vulnerabilities

**Status Badges:**
```markdown
![Dependency Review](https://github.com/YOUR_USERNAME/minerva/workflows/Dependency%20Review/badge.svg)
```

---

## Setup Requirements

### 1. Repository Secrets
No additional secrets required - workflows use `GITHUB_TOKEN` which is automatically provided.

### 2. Repository Variables (Optional)
Create these in Settings → Secrets and variables → Actions → Variables:

- `NEXT_PUBLIC_API_URL` - Frontend API endpoint (default: `http://localhost:8080/api`)

### 3. Backend Requirements
Add to `apps/backend/build.gradle.kts`:

```kotlin
plugins {
    id("org.owasp.dependencycheck") version "8.4.0"
}

dependencyCheck {
    format = "HTML"
    failBuildOnCVSS = 7.0f
}
```

### 4. Frontend Requirements
Ensure these scripts exist in `apps/frontend/package.json`:

```json
{
  "scripts": {
    "build": "next build",
    "lint": "next lint",
    "type-check": "tsc --noEmit",
    "test": "jest" // or vitest
  }
}
```

### 5. Docker Requirements
Create Dockerfiles:
- `apps/backend/Dockerfile`
- `apps/frontend/Dockerfile`

Example backend Dockerfile:
```dockerfile
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Example frontend Dockerfile:
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV production
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
EXPOSE 3000
CMD ["npm", "start"]
```

---

## Permissions

All workflows use minimal required permissions:
- `contents: read` - Read repository contents
- `packages: write` - Push Docker images (docker-build.yml only)
- `security-events: write` - Upload SARIF results (CodeQL, dependency-review only)
- `pull-requests: write` - Comment on PRs (dependency-review only)

---

## Concurrency Control

All workflows use concurrency groups to cancel outdated runs:
```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true
```

This prevents wasting resources on outdated commits.

---

## Artifacts Retention

- **Test reports**: 14 days
- **Build artifacts**: 7 days
- **Security reports**: 14 days

---

## Troubleshooting

### CI workflow not triggering
- Check if paths filter is blocking it
- Verify branch name matches trigger patterns

### Docker build fails
- Ensure Dockerfiles exist in correct locations
- Check multi-platform build support

### CodeQL analysis timeout
- Default timeout is 360 minutes
- Large codebases may need optimization

### Dependency review false positives
- Add suppression file: `suppression.xml` in project root
- Adjust severity threshold in workflow file

---

## Contributing

When modifying workflows:
1. Test in a feature branch first
2. Use `workflow_dispatch` trigger for manual testing
3. Check workflow logs for errors
4. Update this README if adding new workflows

---

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
