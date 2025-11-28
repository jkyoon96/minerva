#!/usr/bin/env python3
"""
CSV를 GitHub Issues로 변환하는 스크립트
Usage: python scripts/csv-to-issues.py [--epic E1] [--priority P0] [--dry-run]
"""

import csv
import subprocess
import sys
import time
import os
import argparse

# 출력 버퍼링 비활성화
sys.stdout.reconfigure(line_buffering=True)

CSV_FILE = "docs/09-git-issues-tasks.csv"
MAPPING_FILE = "scripts/issues-mapping.txt"
DELAY = 2  # API Rate Limit 방지 (초)

def load_mapping():
    """매핑 파일에서 Task ID -> Issue # 로드"""
    mapping = {}
    if os.path.exists(MAPPING_FILE):
        with open(MAPPING_FILE, 'r', encoding='utf-8') as f:
            for line in f:
                if '=' in line and not line.startswith('#'):
                    key, val = line.strip().split('=', 1)
                    mapping[key] = val
    return mapping

def save_mapping(task_id, issue_num):
    """매핑 저장"""
    with open(MAPPING_FILE, 'a', encoding='utf-8') as f:
        f.write(f"{task_id}={issue_num}\n")

def run_gh(args):
    """gh CLI 실행"""
    result = subprocess.run(['gh'] + args, capture_output=True, text=True)
    return result.returncode, result.stdout.strip(), result.stderr.strip()

def get_size_label(sp):
    """Story Points -> Size Label"""
    mapping = {'1': 'size:xs', '2': 'size:s', '3': 'size:m', '5': 'size:l', '8': 'size:xl'}
    return mapping.get(str(sp), 'size:s')

def get_epic_label(epic):
    """Epic -> Label"""
    mapping = {
        'E1': 'epic:e1-auth', 'E2': 'epic:e2-course', 'E3': 'epic:e3-live',
        'E4': 'epic:e4-active', 'E5': 'epic:e5-assessment', 'E6': 'epic:e6-analytics'
    }
    return mapping.get(epic, '')

def get_type_label(labels):
    """Type Label 추출"""
    if '[DB]' in labels: return 'type:db'
    if '[BE]' in labels: return 'type:be'
    if '[FE]' in labels: return 'type:fe'
    if '[DOC]' in labels: return 'type:doc'
    if '[INFRA]' in labels: return 'type:infra'
    return ''

def get_priority_label(priority):
    """Priority -> Label"""
    mapping = {'P0': 'priority:p0-mvp', 'P1': 'priority:p1-v1', 'P2': 'priority:p2-v2'}
    return mapping.get(priority, '')

def get_milestone(priority):
    """Priority -> Milestone"""
    mapping = {'P0': 'MVP (v0.1)', 'P1': 'v1.0', 'P2': 'v2.0'}
    return mapping.get(priority, '')

def get_epic_name(epic):
    """Epic 이름"""
    mapping = {
        'E1': 'E1: 사용자 인증', 'E2': 'E2: 코스 관리', 'E3': 'E3: 실시간 세미나',
        'E4': 'E4: 액티브 러닝', 'E5': 'E5: 평가/피드백', 'E6': 'E6: 학습 분석'
    }
    return mapping.get(epic, epic)

def get_story_name(story):
    """Story 이름"""
    mapping = {
        'E1-S1': '회원가입', 'E1-S2': '로그인', 'E1-S3': '소셜 로그인',
        'E1-S4': '2단계 인증', 'E1-S5': '비밀번호 재설정', 'E1-S6': '역할 기반 접근 제어',
        'E1-S7': '프로필 관리', 'E2-S0': '대시보드', 'E2-S1': '코스 생성/수정',
        'E2-S2': '수강생 관리', 'E2-S3': '세션 관리', 'E2-S4': '과제 관리',
        'E2-S5': '콘텐츠 라이브러리', 'E2-S6': '성적 관리', 'E3-S1': '세션 시작/참여',
        'E3-S2': '화상 기능', 'E3-S3': '화면 공유', 'E3-S4': '채팅',
        'E3-S5': '녹화/재생', 'E3-S6': '레이아웃 관리', 'E4-S1': '투표/설문',
        'E4-S2': '퀴즈', 'E4-S3': '분반 토론', 'E4-S4': '화이트보드',
        'E4-S5': '토론/지명', 'E5-S1': '퀴즈 결과', 'E5-S2': 'AI 채점',
        'E5-S3': '코드 평가', 'E5-S4': '동료 평가', 'E5-S5': '참여도 분석',
        'E6-S1': '실시간 분석', 'E6-S2': '리포트', 'E6-S3': '조기 경보',
        'E6-S4': '네트워크 분석'
    }
    return mapping.get(story, story)

def get_acceptance_criteria(type_label):
    """Type별 Acceptance Criteria"""
    if type_label == 'type:db':
        return """### Database 요구사항
- [ ] 스키마 변경사항이 마이그레이션 파일로 작성됨
- [ ] 롤백 마이그레이션이 포함됨
- [ ] 인덱스 및 제약조건이 적절히 설정됨
- [ ] 테스트 데이터 시딩 스크립트 포함

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 문서화 완료"""

    elif type_label == 'type:be':
        return """### Backend 요구사항
- [ ] API 엔드포인트가 명세대로 구현됨
- [ ] 입력값 유효성 검사 구현
- [ ] 에러 핸들링 및 적절한 HTTP 상태 코드 반환
- [ ] 단위 테스트 작성 (커버리지 80% 이상)
- [ ] API 문서 (Swagger/OpenAPI) 업데이트

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 보안 검토 완료"""

    elif type_label == 'type:fe':
        return """### Frontend 요구사항
- [ ] 와이어프레임 디자인대로 UI 구현
- [ ] 반응형 디자인 적용 (모바일/태블릿/데스크톱)
- [ ] 접근성 가이드라인 준수 (WCAG 2.1 AA)
- [ ] 로딩/에러 상태 처리

### 테스트 요구사항
- [ ] 컴포넌트 단위 테스트 작성
- [ ] 스토리북 스토리 추가

### 품질 요구사항
- [ ] 코드 리뷰 완료
- [ ] 크로스 브라우저 테스트"""

    elif type_label == 'type:doc':
        return """### Documentation 요구사항
- [ ] 문서 초안 작성
- [ ] 기술 검토 완료
- [ ] 스크린샷/다이어그램 포함 (필요시)

### 품질 요구사항
- [ ] 문서 리뷰 완료
- [ ] 오타/문법 검토"""

    return """### 기본 요구사항
- [ ] 기능 구현 완료
- [ ] 테스트 작성
- [ ] 코드 리뷰 완료"""

def ensure_milestones():
    """Milestone은 gh CLI에서 지원하지 않으므로 스킵"""
    print("Note: Milestone은 GitHub 웹에서 수동 생성 필요")
    print("  - MVP (v0.1): P0 우선순위")
    print("  - v1.0: P1 우선순위")
    print("  - v2.0: P2 우선순위")
    print()

def create_issue(row, mapping, dry_run=False):
    """Issue 생성"""
    task_id = row['Task ID'].strip()
    epic = row['Epic'].strip()
    story = row['Story'].strip()
    title = row['Task Title'].strip()
    labels_csv = row['Labels'].strip()
    description = row['Description'].strip()
    sp = row['Story Points'].strip()
    priority = row['Priority'].strip()
    deps = row['Dependencies'].strip()
    ref_docs = row['Reference Docs'].strip()
    wireframes = row['Wireframe Files'].strip()

    # 이미 생성된 Issue 확인
    if task_id in mapping:
        print(f"[SKIP] Already exists: {task_id} -> #{mapping[task_id]}")
        return

    # Labels 구성
    issue_labels = []
    epic_label = get_epic_label(epic)
    type_label = get_type_label(labels_csv)
    size_label = get_size_label(sp)
    priority_label = get_priority_label(priority)

    if epic_label: issue_labels.append(epic_label)
    if type_label: issue_labels.append(type_label)
    if size_label: issue_labels.append(size_label)
    if priority_label: issue_labels.append(priority_label)

    # 의존성 상태
    if deps:
        issue_labels.append('status:blocked')
    else:
        issue_labels.append('status:ready')

    # Milestone
    milestone = get_milestone(priority)

    # 의존성 해석
    resolved_deps = ""
    if deps:
        for dep in deps.split(';'):
            dep = dep.strip()
            if dep:
                if dep in mapping:
                    resolved_deps += f"- [ ] #{mapping[dep]} ({dep})\n"
                else:
                    resolved_deps += f"- [ ] {dep} (미생성)\n"
    else:
        resolved_deps = "- 없음\n"

    # Reference Docs
    ref_section = ""
    if ref_docs:
        ref_section = "## Reference Documents\n\n"
        for doc in ref_docs.split(';'):
            doc = doc.strip()
            if doc:
                ref_section += f"- `docs/{doc}`\n"
        ref_section += "\n"

    # Wireframe
    wireframe_section = ""
    if wireframes:
        wireframe_section = f"""## Wireframe Files

```
docs/wireframes/{wireframes}
```

"""

    # Acceptance Criteria
    acceptance = get_acceptance_criteria(type_label)

    # Issue Body
    body = f"""## Task 개요

| 항목 | 값 |
|------|-----|
| **Task ID** | {task_id} |
| **Epic** | {get_epic_name(epic)} |
| **Story** | {get_story_name(story)} |
| **Story Points** | {sp} |
| **Priority** | {priority} |

## Description

{description}

## Dependencies

### Blocked By (선행 작업)
{resolved_deps}

{ref_section}{wireframe_section}## Acceptance Criteria

{acceptance}

---
> Generated from: docs/09-git-issues-tasks.csv"""

    issue_title = f"[{task_id}] {title}"

    if dry_run:
        print(f"[DRY-RUN] Would create: {issue_title}")
        print(f"  Labels: {','.join(issue_labels)}")
        print(f"  Milestone: {milestone}")
        print()
        return

    # Issue 생성 (Milestone은 gh CLI에서 지원 안함)
    args = ['issue', 'create', '--title', issue_title, '--body', body, '--label', ','.join(issue_labels)]

    code, stdout, stderr = run_gh(args)

    if code == 0 and 'github.com' in stdout:
        # Issue 번호 추출
        issue_num = stdout.split('/')[-1]
        save_mapping(task_id, issue_num)
        mapping[task_id] = issue_num
        print(f"[OK] Created: {issue_title} -> #{issue_num}")
    else:
        print(f"[ERROR] Failed to create: {issue_title}")
        if stderr:
            print(f"  {stderr}")

    # Rate limit 방지
    time.sleep(DELAY)

def main():
    parser = argparse.ArgumentParser(description='CSV to GitHub Issues')
    parser.add_argument('--epic', help='Filter by Epic (e.g., E1)')
    parser.add_argument('--priority', help='Filter by Priority (e.g., P0)')
    parser.add_argument('--dry-run', action='store_true', help='Dry run mode')
    args = parser.parse_args()

    print("=== CSV to GitHub Issues 변환 스크립트 ===")
    print(f"CSV File: {CSV_FILE}")
    print(f"Epic Filter: {args.epic or '전체'}")
    print(f"Priority Filter: {args.priority or '전체'}")
    print(f"Dry Run: {args.dry_run}")
    print()

    # 인증 확인
    code, _, _ = run_gh(['auth', 'status'])
    if code != 0:
        print("Error: GitHub CLI 인증이 필요합니다.")
        print("실행: gh auth login")
        sys.exit(1)

    # Milestone 생성
    if not args.dry_run:
        ensure_milestones()

    # 매핑 로드
    mapping = load_mapping()

    # CSV 읽기
    print("=== Issue 생성 시작 ===")
    print()

    with open(CSV_FILE, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        count = 0
        for row in reader:
            # 필터 적용
            if args.epic and row['Epic'].strip() != args.epic:
                continue
            if args.priority and row['Priority'].strip() != args.priority:
                continue

            create_issue(row, mapping, args.dry_run)
            count += 1

    print()
    print("=== 완료 ===")
    print(f"처리된 Task 수: {count}")
    print(f"매핑 파일: {MAPPING_FILE}")

if __name__ == '__main__':
    main()
