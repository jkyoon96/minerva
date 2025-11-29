# CI/CD Developer Guide

Quick reference for developers working with the Minerva/EduForum CI/CD pipeline.

## Table of Contents
1. [Before You Commit](#before-you-commit)
2. [Understanding Workflow Triggers](#understanding-workflow-triggers)
3. [Common Scenarios](#common-scenarios)
4. [Troubleshooting](#troubleshooting)
5. [Best Practices](#best-practices)

---

## Before You Commit

### Backend Changes (Java/Spring Boot)

```bash
# In apps/backend/

# Run tests locally
./gradlew test

# Check for dependency vulnerabilities
./gradlew dependencyCheckAnalyze

# Build the project
./gradlew build

# Format code (if checkstyle is configured)
./gradlew checkstyleMain
```

### Frontend Changes (Next.js/React)

```bash
# In apps/frontend/

# Install dependencies
npm ci

# Run linting
npm run lint

# Type checking
npm run type-check

# Run tests
npm test

# Build the project
npm run build

# Audit dependencies
npm audit
```

---

## Understanding Workflow Triggers

### CI Workflow (`ci.yml`)
- **When it runs:**
  - Push to `main` or `develop` branches
  - Pull requests to `main` or `develop` branches

- **Path filtering:**
  - Backend jobs only run if `apps/backend/**` or Gradle files change
  - Frontend jobs only run if `apps/frontend/**` or package files change

- **What it does:**
  1. Detects which parts changed (backend/frontend)
  2. Runs parallel jobs for build and test
  3. Uploads test reports and build artifacts

### Docker Build Workflow (`docker-build.yml`)
- **When it runs:**
  - Push to `main` branch
  - Version tags (e.g., `v1.0.0`)
  - Manual trigger via GitHub UI

- **What it does:**
  1. Builds Docker images for backend and frontend
  2. Pushes to GitHub Container Registry (ghcr.io)
  3. Tags images with version/branch/SHA

### CodeQL Workflow (`codeql.yml`)
- **When it runs:**
  - Push to `main` or `develop` branches
  - Pull requests to `main` or `develop` branches
  - Every Monday at 00:00 UTC (scheduled)
  - Manual trigger

- **What it does:**
  1. Analyzes Java and TypeScript code for security issues
  2. Uploads SARIF results to GitHub Security tab
  3. Fails if critical vulnerabilities found

### Dependency Review Workflow (`dependency-review.yml`)
- **When it runs:**
  - Pull requests to `main` or `develop` branches

- **What it does:**
  1. Reviews new/changed dependencies
  2. Checks for known vulnerabilities
  3. Denies certain licenses (GPL, AGPL)
  4. Comments summary in PR

---

## Common Scenarios

### Scenario 1: Creating a Pull Request

1. **Create your feature branch:**
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. **Make your changes and commit:**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   git push origin feature/my-new-feature
   ```

3. **Create PR on GitHub**
   - CI workflow runs automatically
   - Dependency review checks new dependencies
   - CodeQL analyzes code for security issues

4. **Fix any CI failures:**
   - Check workflow logs in GitHub Actions tab
   - Fix issues locally and push again
   - CI re-runs automatically

### Scenario 2: Merging to Main

1. **After PR approval and CI passes:**
   ```bash
   git checkout main
   git merge feature/my-new-feature
   git push origin main
   ```

2. **Automatic processes:**
   - CI workflow runs on main branch
   - Docker images are built and pushed
   - CodeQL analysis runs
   - Images tagged as `latest` and `main-<sha>`

### Scenario 3: Creating a Release

1. **Create and push a version tag:**
   ```bash
   git checkout main
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

2. **Automatic processes:**
   - Docker images built with version tags
   - Images available as:
     - `ghcr.io/YOUR_USERNAME/minerva/backend:v1.0.0`
     - `ghcr.io/YOUR_USERNAME/minerva/backend:v1.0`
     - `ghcr.io/YOUR_USERNAME/minerva/backend:v1`
     - Same for frontend

### Scenario 4: Manual Docker Build

1. **Go to GitHub Actions tab**
2. **Select "Docker Build" workflow**
3. **Click "Run workflow"**
4. **Select branch and click "Run workflow" button**

### Scenario 5: Fixing a Security Vulnerability

1. **Check Security tab on GitHub:**
   - CodeQL alerts
   - Dependabot alerts
   - Dependency review comments in PRs

2. **Fix the vulnerability:**
   - Update dependency versions
   - Refactor vulnerable code
   - Add suppression if false positive

3. **Verify the fix:**
   - Run security scans locally
   - Create PR and check workflow results
   - Verify alert is resolved in Security tab

---

## Troubleshooting

### CI Workflow Failures

#### Backend Build Fails
```bash
# Common causes:
# 1. Compilation errors
./gradlew build --stacktrace

# 2. Test failures
./gradlew test --info

# 3. Gradle cache issues
./gradlew clean build --no-daemon
```

#### Backend Tests Fail
```bash
# Run tests with detailed output
./gradlew test --info --stacktrace

# Run specific test class
./gradlew test --tests com.eduforum.api.YourTestClass

# Check test reports
open apps/backend/build/reports/tests/test/index.html
```

#### Frontend Build Fails
```bash
# Common causes:
# 1. TypeScript errors
npm run type-check

# 2. Linting errors
npm run lint -- --fix

# 3. Build errors
npm run build

# 4. Dependency issues
rm -rf node_modules package-lock.json
npm install
```

#### Frontend Tests Fail
```bash
# Run tests with verbose output
npm test -- --verbose

# Run specific test file
npm test -- path/to/test.spec.ts

# Update snapshots if needed
npm test -- -u
```

### Docker Build Failures

#### Build Context Issues
```bash
# Test Docker build locally
cd apps/backend
docker build -t minerva-backend .

cd ../frontend
docker build -t minerva-frontend .
```

#### Multi-platform Build Issues
```bash
# Set up buildx
docker buildx create --use

# Test multi-platform build
docker buildx build --platform linux/amd64,linux/arm64 -t test .
```

### CodeQL Failures

#### Timeout Issues
- CodeQL analysis can take a long time for large codebases
- Default timeout is 360 minutes
- Consider excluding test files or generated code

#### False Positives
- Review CodeQL results in Security tab
- Add suppression comments in code:
  ```java
  // lgtm[java/sql-injection]
  String query = buildQuery();
  ```

### Dependency Review Failures

#### Known Vulnerabilities
```bash
# Backend: Check Gradle dependencies
./gradlew dependencyCheckAnalyze
open apps/backend/build/reports/dependency-check-report.html

# Frontend: Check npm dependencies
npm audit
npm audit fix
```

#### License Issues
- Some licenses (GPL, AGPL) are blocked
- Check dependency licenses:
  ```bash
  # Backend
  ./gradlew dependencies

  # Frontend
  npm ls --all
  ```
- Replace problematic dependencies with alternatives

#### False Positive Suppressions
- Add to `suppression.xml` in project root:
  ```xml
  <suppress>
      <notes>Reason for suppression</notes>
      <filePath regex="true">.*\bdependency-name-.*\.jar</filePath>
      <cve>CVE-2023-12345</cve>
  </suppress>
  ```

---

## Best Practices

### 1. Commit Hygiene
- Write clear commit messages (conventional commits)
- Keep commits atomic and focused
- Run tests locally before pushing
- Fix linting/type errors before committing

### 2. Branch Strategy
- Create feature branches from `develop`
- Merge to `develop` for integration testing
- Merge to `main` only for releases
- Use semantic versioning for tags

### 3. Pull Request Reviews
- Wait for CI to pass before requesting review
- Address all CI failures before merge
- Review security alerts and dependency warnings
- Keep PRs small and focused

### 4. Dependency Management
- Regular dependency updates (weekly/monthly)
- Review security advisories
- Test after dependency updates
- Document any suppressions

### 5. Docker Images
- Tag releases with semantic versions
- Use specific versions in production (not `latest`)
- Review image sizes and optimize
- Scan images for vulnerabilities

### 6. Security
- Enable Dependabot alerts
- Review CodeQL results regularly
- Keep dependencies up to date
- Rotate secrets and tokens

### 7. Performance
- Use caching in workflows (Gradle, npm)
- Path filtering for monorepo efficiency
- Parallel job execution
- Cancel outdated workflow runs

---

## Useful Commands

### GitHub CLI (gh)

```bash
# View workflow runs
gh run list --workflow=ci.yml

# View specific run
gh run view <run-id>

# Watch a running workflow
gh run watch

# Re-run a failed workflow
gh run rerun <run-id>

# Download artifacts
gh run download <run-id>
```

### Local Testing

```bash
# Run entire CI locally with act
act -W .github/workflows/ci.yml

# Run specific job
act -j backend-build

# Test workflow syntax
actionlint .github/workflows/*.yml
```

---

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Build Action](https://github.com/gradle/gradle-build-action)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

## Getting Help

1. **Check workflow logs:**
   - GitHub Actions tab in repository
   - Click on failed job for details

2. **Ask the team:**
   - Post in team chat with workflow link
   - Include error messages and logs

3. **Create an issue:**
   - Use template: `.github/ISSUE_TEMPLATE/ci-cd-issue.md`
   - Include workflow run ID and logs
