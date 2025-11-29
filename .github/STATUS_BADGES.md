# GitHub Actions Status Badges

Add these badges to your README.md to display workflow status.

## Standard Badges

### CI Pipeline
```markdown
[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml)
```

### Docker Build
```markdown
[![Docker Build](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml)
```

### CodeQL Security Analysis
```markdown
[![CodeQL](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml)
```

### Dependency Review
```markdown
[![Dependency Review](https://github.com/YOUR_USERNAME/minerva/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/dependency-review.yml)
```

---

## Branch-Specific Badges

### Main Branch
```markdown
[![CI - Main](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml?query=branch%3Amain)
```

### Develop Branch
```markdown
[![CI - Develop](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml?query=branch%3Adevelop)
```

---

## Combined Badge Section

Copy this complete section to your README.md:

```markdown
## Build Status

[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml)
[![Docker Build](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml)
[![CodeQL](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml)
[![Dependency Review](https://github.com/YOUR_USERNAME/minerva/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/dependency-review.yml)
```

---

## Custom Badge Styling

### With Event Filter
```markdown
[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml?query=event%3Apush)
```

### With Status Filter
```markdown
[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg?event=push&status=success)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml?query=event%3Apush+status%3Asuccess)
```

---

## Docker Image Badges (GHCR)

### Backend Image
```markdown
[![Backend Docker Image Version](https://ghcr-badge.egpl.dev/YOUR_USERNAME/minerva/backend/latest_tag?trim=major&label=backend)](https://github.com/YOUR_USERNAME/minerva/pkgs/container/minerva%2Fbackend)
```

### Frontend Image
```markdown
[![Frontend Docker Image Version](https://ghcr-badge.egpl.dev/YOUR_USERNAME/minerva/frontend/latest_tag?trim=major&label=frontend)](https://github.com/YOUR_USERNAME/minerva/pkgs/container/minerva%2Ffrontend)
```

---

## Additional Badges

### License
```markdown
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
```

### Java Version
```markdown
[![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)](https://adoptium.net/)
```

### Node.js Version
```markdown
[![Node](https://img.shields.io/badge/Node.js-20-green?logo=node.js)](https://nodejs.org/)
```

### Spring Boot Version
```markdown
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green?logo=springboot)](https://spring.io/projects/spring-boot)
```

### Next.js Version
```markdown
[![Next.js](https://img.shields.io/badge/Next.js-14-black?logo=next.js)](https://nextjs.org/)
```

---

## Complete Example for README.md

```markdown
# Minerva - EduForum

[![CI](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/ci.yml)
[![Docker Build](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/docker-build.yml)
[![CodeQL](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/YOUR_USERNAME/minerva/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Node.js](https://img.shields.io/badge/Node.js-20-green?logo=node.js)](https://nodejs.org/)
[![Next.js](https://img.shields.io/badge/Next.js-14-black?logo=next.js)](https://nextjs.org/)

> Online learning platform inspired by Minerva University's Active Learning Forum

## Overview

...
```

---

## Notes

1. Replace `YOUR_USERNAME` with your GitHub username
2. Replace `minerva` with your repository name if different
3. Badges update automatically when workflows run
4. Click badges to view workflow details
5. Use shields.io for custom badges: https://shields.io/
