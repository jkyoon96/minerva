# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2024-11-29

### Added

#### Phase 5: Core Feature Implementation

**E1: Authentication (사용자 인증)**
- User registration with email verification
- JWT-based login/logout
- Two-Factor Authentication (TOTP with backup codes)
- Password reset flow
- Profile management (avatar, email change, password change)
- Role-based access control (RBAC)
- Login attempt tracking with account lockout (5 failures = 15 min)
- Admin user management UI

**E2: Course Management (코스 관리)**
- Course CRUD operations
- Course session management
- Student enrollment (individual and bulk CSV import)
- TA assignment with granular permissions
- Grading criteria (rubric) management
- Content library with file/folder management
- Calendar export (iCal/ICS format)
- Waitlist management

**E3: Live Seminar (실시간 세미나)**
- WebRTC-based video conferencing
- Screen sharing
- Real-time chat
- Hand raise functionality
- Session recording
- Participant management

**E4: Active Learning (액티브 러닝)**
- Real-time polls and quizzes
- Breakout rooms for group discussions
- Collaborative whiteboard
- Discussion threads
- Emoji reactions

**E5: Assessment (평가 및 피드백)**
- Assignment submission and grading
- Rubric-based evaluation
- Peer review system
- Grade management
- Feedback system

**E6: Analytics (학습 분석)**
- Real-time participation tracking
- Learning engagement metrics
- At-risk student alerts
- Course analytics dashboard
- Export reports

#### Infrastructure

**Email Service**
- Multiple provider support (Console, SMTP, SendGrid)
- Async email queue with scheduled processing
- HTML email templates (welcome, verification, password reset, invitation)

**File Storage**
- Multiple backend support (Local, AWS S3)
- File/folder management with permissions
- Drag-and-drop upload
- File search and organization

**DevOps**
- Docker multi-stage builds (Backend, Frontend)
- Docker Compose for development and production
- GitHub Actions CI/CD pipeline
- CodeQL security analysis
- Dependency vulnerability scanning
- Test infrastructure (JUnit 5, Jest, React Testing Library)

### Technical Details

**Backend Stack**
- Spring Boot 3.2.1
- Java 17
- Spring Security with JWT
- PostgreSQL 15
- Flyway migrations
- WebSocket (STOMP)

**Frontend Stack**
- Next.js 14 (App Router)
- React 18
- TypeScript
- Tailwind CSS
- shadcn/ui
- Zustand

**Database Migrations**
- V001: Core schema (users, roles, courses, enrollments)
- V002: Seminar schema (rooms, participants, chat)
- V003: Active learning schema (polls, quizzes, breakouts)
- V004: Assessment schema (assignments, submissions, grades)
- V005: Analytics schema (metrics, events, alerts)
- V006: Core updates and indexes
- V007: Two-factor authentication
- V008: Email change tokens
- V009: Additional features (login attempts, grading criteria, TA)
- V010: Email queue
- V011: File storage

### Statistics

- **Total Commits**: 15+
- **Total Files Changed**: 300+
- **Lines of Code**: 50,000+
- **API Endpoints**: 100+
- **Database Tables**: 40+
- **React Components**: 80+

## [0.1.0] - 2024-11-01

### Added
- Initial project setup
- Monorepo structure (apps/backend, apps/frontend)
- Basic documentation
- Wireframe designs

---

[Unreleased]: https://github.com/jkyoon96/minerva/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/jkyoon96/minerva/releases/tag/v1.0.0
[0.1.0]: https://github.com/jkyoon96/minerva/releases/tag/v0.1.0
