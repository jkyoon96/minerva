# CI/CD Setup Checklist

Complete this checklist to ensure the CI/CD pipeline is properly configured.

## Prerequisites

- [ ] GitHub repository created
- [ ] Repository permissions: Settings → Actions → General → "Read and write permissions" enabled
- [ ] Branch protection rules configured (optional but recommended)

---

## 1. Backend Configuration

### Gradle Configuration

- [ ] **File: `apps/backend/build.gradle.kts`** (or `build.gradle`)

Add OWASP Dependency Check plugin:

```kotlin
plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.owasp.dependencycheck") version "8.4.0"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

dependencyCheck {
    formats = listOf("HTML", "SARIF")
    failBuildOnCVSS = 7.0f
}
```

### Test Configuration

- [ ] **File: `apps/backend/src/test/resources/application-test.yml`**

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/eduforum_test}
    username: ${SPRING_DATASOURCE_USERNAME:eduforum}
    password: ${SPRING_DATASOURCE_PASSWORD:eduforum_test_password}
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  profiles:
    active: test
```

### Dockerfile

- [ ] **File: `apps/backend/Dockerfile`** exists and is valid

Verify with:
```bash
cd apps/backend
docker build -t minerva-backend-test .
```

---

## 2. Frontend Configuration

### Package.json Scripts

- [ ] **File: `apps/frontend/package.json`** contains required scripts:

```json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "type-check": "tsc --noEmit",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage"
  }
}
```

### TypeScript Configuration

- [ ] **File: `apps/frontend/tsconfig.json`** exists

### Jest/Testing Configuration

- [ ] **File: `apps/frontend/jest.config.js`** exists (or vitest.config.ts)

### Dockerfile

- [ ] **File: `apps/frontend/Dockerfile`** exists and is valid

Verify with:
```bash
cd apps/frontend
docker build -t minerva-frontend-test .
```

---

## 3. GitHub Settings

### Repository Settings

- [ ] Go to Settings → Actions → General
- [ ] Workflow permissions → "Read and write permissions" ✓
- [ ] "Allow GitHub Actions to create and approve pull requests" ✓ (optional)

### Branch Protection (Recommended)

- [ ] Go to Settings → Branches → Add rule
- [ ] Branch name pattern: `main`
- [ ] ✓ Require a pull request before merging
- [ ] ✓ Require status checks to pass before merging
  - Select: `ci-success`, `backend-build`, `backend-test`, `frontend-build`, `frontend-test`
- [ ] ✓ Require conversation resolution before merging
- [ ] ✓ Do not allow bypassing the above settings

### Secrets and Variables (Optional)

- [ ] Go to Settings → Secrets and variables → Actions → Variables
- [ ] Add variable: `NEXT_PUBLIC_API_URL` (e.g., `https://api.example.com`)

### Security Settings

- [ ] Go to Settings → Code security and analysis
- [ ] ✓ Enable Dependency graph
- [ ] ✓ Enable Dependabot alerts
- [ ] ✓ Enable Dependabot security updates
- [ ] ✓ Enable Code scanning (CodeQL)

---

## 4. Workflow Files

Verify all workflow files exist:

- [ ] `.github/workflows/ci.yml`
- [ ] `.github/workflows/docker-build.yml`
- [ ] `.github/workflows/codeql.yml`
- [ ] `.github/workflows/dependency-review.yml`

Quick check:
```bash
ls -la .github/workflows/
```

---

## 5. Additional Files

- [ ] `suppression.xml` - OWASP suppression file (in project root)
- [ ] `.github/workflows/README.md` - Workflow documentation
- [ ] `.github/CICD_GUIDE.md` - Developer guide
- [ ] `.github/STATUS_BADGES.md` - Badge templates

---

## 6. Initial Testing

### Test CI Workflow

1. [ ] Create a test branch:
   ```bash
   git checkout -b test/ci-setup
   ```

2. [ ] Make a small change (e.g., update README):
   ```bash
   echo "# CI/CD Test" >> README.md
   git add README.md
   git commit -m "test: verify CI workflow"
   git push origin test/ci-setup
   ```

3. [ ] Create a Pull Request
4. [ ] Verify workflows run:
   - [ ] CI workflow triggers
   - [ ] Backend build/test runs (if backend files changed)
   - [ ] Frontend build/test runs (if frontend files changed)
   - [ ] Dependency Review runs
   - [ ] CodeQL analysis runs

### Test Docker Build

1. [ ] Merge PR to main or push directly to main:
   ```bash
   git checkout main
   git merge test/ci-setup
   git push origin main
   ```

2. [ ] Verify Docker Build workflow runs
3. [ ] Check GitHub Packages for built images:
   - Go to repository → Packages
   - Verify `minerva/backend` and `minerva/frontend` packages exist

### Test Release Process

1. [ ] Create a version tag:
   ```bash
   git tag -a v0.1.0 -m "Initial CI/CD setup"
   git push origin v0.1.0
   ```

2. [ ] Verify Docker images built with version tags:
   - `v0.1.0`, `v0.1`, `v0`

---

## 7. Cleanup and Documentation

### Update README.md

- [ ] Add status badges to `README.md`:

```markdown
# Minerva - EduForum

[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml)
[![Docker Build](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml)
[![CodeQL](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml)

...
```

### Create Documentation

- [ ] Add CI/CD section to project documentation
- [ ] Document Docker image usage
- [ ] Create runbook for common issues

### Team Communication

- [ ] Share `.github/CICD_GUIDE.md` with team
- [ ] Conduct CI/CD overview meeting
- [ ] Set up notifications (Slack, Discord, etc.)

---

## 8. Monitoring and Maintenance

### Regular Tasks

- [ ] **Weekly:**
  - Review failed workflow runs
  - Check security alerts (Dependabot, CodeQL)
  - Update dependencies if needed

- [ ] **Monthly:**
  - Review workflow performance and optimization
  - Update workflow actions to latest versions
  - Clean up old artifacts and packages

- [ ] **Quarterly:**
  - Review and update branch protection rules
  - Audit CI/CD costs and usage
  - Update documentation

### Metrics to Monitor

- [ ] Workflow success rate
- [ ] Average workflow duration
- [ ] Test coverage trends
- [ ] Dependency vulnerability count
- [ ] Docker image sizes

---

## 9. Advanced Configuration (Optional)

### Notifications

- [ ] Set up Slack notifications for workflow failures
- [ ] Configure email notifications
- [ ] Set up Discord webhooks

### Deployment

- [ ] Add deployment workflow (e.g., to AWS, Azure, GCP)
- [ ] Configure staging environment
- [ ] Set up automatic deployments

### Performance

- [ ] Add performance testing workflows
- [ ] Configure load testing
- [ ] Monitor build times and optimize

### Quality Gates

- [ ] Add SonarQube integration
- [ ] Configure code coverage thresholds
- [ ] Add complexity analysis

---

## Troubleshooting

### Common Issues

**Issue:** Workflows not triggering
- **Solution:** Check workflow file syntax with `actionlint`
- **Solution:** Verify branch names match trigger patterns

**Issue:** Docker build fails with permission errors
- **Solution:** Enable write permissions in Settings → Actions

**Issue:** Tests fail in CI but pass locally
- **Solution:** Check environment variables and database configuration
- **Solution:** Verify test isolation and cleanup

**Issue:** CodeQL timeout
- **Solution:** Exclude test files or increase timeout limit

---

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax Reference](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [CICD_GUIDE.md](./.github/CICD_GUIDE.md)
- [Workflow README](./.github/workflows/README.md)

---

## Sign-off

- [ ] All checklist items completed
- [ ] CI/CD pipeline tested and working
- [ ] Team trained on CI/CD workflows
- [ ] Documentation updated

**Completed by:** _________________
**Date:** _________________
**Reviewed by:** _________________
