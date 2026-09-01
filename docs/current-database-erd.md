# LawDingBE 현재 데이터베이스 ERD

## 문서 기준

- 기준 스키마: Git에 반영된 Flyway `V1`, Java `V2`, SQL `V3`
- 대상 DB: PostgreSQL
- 테이블 수: 11개
- 연차 수량은 일수가 아니라 모두 **분(minutes)** 단위로 저장됩니다.

## 전체 관계 한눈에 보기

```mermaid
flowchart LR
    subgraph USER[사용자 영역]
        U[users<br/>사용자]
        P[user_leave_policies<br/>연차 정책]
        N[user_notifications<br/>알림]
        U --> P
        U --> N
    end

    subgraph LEAVE[연차 관리 영역]
        B[leave_yearly_balances<br/>기간별 연차 잔액]
        G[leave_grants<br/>연차 발생 원장]
        E[calendar_events<br/>일정]
        A[calendar_event_leave_allocations<br/>일정별 원장 차감]
        B --> G
        B --> E
        G --> A
        E --> A
    end

    subgraph CONTENT[콘텐츠 영역]
        C[dictionary_category<br/>사전 카테고리]
        D[dictionary<br/>사전]
        C --> D
    end

    U --> B
    U --> G
    U --> E

    F[feedback<br/>피드백]
    R[recommended_schedules<br/>추천 일정]
```

## 1. 사용자·정책·알림 ERD

```mermaid
erDiagram
    USERS {
        BIGINT id PK "사용자 ID"
        VARCHAR username "로그인 사용자 식별명"
        VARCHAR email UK "이메일"
        VARCHAR provider "OAuth 제공자"
        VARCHAR nickname "사용자 닉네임"
        TIMESTAMP last_login_at "마지막 로그인 일시"
        VARCHAR refresh_token "리프레시 토큰"
        BOOLEAN onboarding_completed "온보딩 완료 여부"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    USER_LEAVE_POLICIES {
        BIGINT user_id PK,FK "사용자 ID"
        TIMESTAMP accepted_at "정책 동의 일시"
        VARCHAR leave_accrual_basis "연차 산정 기준"
        DATE hire_date "입사일"
        INTEGER fiscal_year_base_month "회계연도 기준 월"
        INTEGER company_size "회사 규모"
        JSON work_pattern "근무시간 패턴"
        JSON break_time_pattern "휴게시간 패턴"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    USER_NOTIFICATIONS {
        BIGINT id PK "알림 ID"
        BIGINT user_id FK "사용자 ID"
        VARCHAR type "알림 유형"
        VARCHAR title "알림 제목"
        VARCHAR message "알림 내용"
        INTEGER granted_minutes "발생 연차 시간(분)"
        VARCHAR event_key UK "중복 방지 사건 키"
        BOOLEAN is_read "읽음 여부"
        TIMESTAMP read_at "읽은 일시"
        TIMESTAMP created_at "생성 일시"
    }

    USERS ||--o| USER_LEAVE_POLICIES : "연차 정책 보유"
    USERS ||--o{ USER_NOTIFICATIONS : "알림 수신"
```

## 2. 연차 잔액·원장·일정 ERD

```mermaid
erDiagram
    USERS {
        BIGINT id PK "사용자 ID"
    }

    LEAVE_YEARLY_BALANCES {
        BIGINT id PK "연차 잔액 ID"
        BIGINT user_id FK "사용자 ID"
        DATE start_date "연차 사용기간 시작일"
        DATE end_date "연차 사용기간 종료일"
        INTEGER weekly_working_days "주간 근무일 수"
        DECIMAL avg_daily_work_hours "일평균 근로시간"
        INTEGER total_leave_minutes "총 연차 시간(분)"
        INTEGER used_leave_minutes "사용 연차 시간(분)"
        BOOLEAN is_finalized "기간 마감 여부"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    LEAVE_GRANTS {
        BIGINT id PK "연차 발생 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT leave_yearly_balance_id FK "연차 잔액 ID"
        VARCHAR grant_type "발생 연차 유형"
        VARCHAR source "발생 출처"
        VARCHAR source_key UK "중복 방지 발생 키"
        INTEGER granted_minutes "최초 발생 시간(분)"
        INTEGER adjusted_minutes "조정 시간(분)"
        INTEGER used_minutes "사용된 시간(분)"
        DATE granted_date "발생일"
        DATE start_date "사용 가능 시작일"
        DATE end_date "사용 가능 종료일"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    CALENDAR_EVENTS {
        BIGINT id PK "일정 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT leave_yearly_balance_id FK "연차 잔액 ID"
        VARCHAR title "일정 제목"
        TEXT description "일정 설명"
        TIMESTAMP start_datetime "일정 시작 일시"
        TIMESTAMP end_datetime "일정 종료 일시"
        INTEGER used_leave_minutes "사용 연차 시간(분)"
        BOOLEAN is_all_day "종일 일정 여부"
        BOOLEAN is_leave_event "연차 일정 여부"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    CALENDAR_EVENT_LEAVE_ALLOCATIONS {
        BIGINT id PK "차감 배분 ID"
        BIGINT calendar_event_id FK "일정 ID"
        BIGINT leave_grant_id FK "연차 발생 ID"
        INTEGER allocated_minutes "해당 발생분에서 차감한 시간(분)"
    }

    USERS ||--o{ LEAVE_YEARLY_BALANCES : "기간별 잔액 보유"
    USERS ||--o{ LEAVE_GRANTS : "연차 발생"
    USERS ||--o{ CALENDAR_EVENTS : "일정 등록"
    LEAVE_YEARLY_BALANCES ||--o{ LEAVE_GRANTS : "발생 내역 포함"
    LEAVE_YEARLY_BALANCES ||--o{ CALENDAR_EVENTS : "사용 일정 포함"
    LEAVE_GRANTS ||--o{ CALENDAR_EVENT_LEAVE_ALLOCATIONS : "차감 원천"
    CALENDAR_EVENTS ||--o{ CALENDAR_EVENT_LEAVE_ALLOCATIONS : "차감 내역"
```

## 3. 사전 ERD

```mermaid
erDiagram
    DICTIONARY_CATEGORY {
        BIGINT id PK "카테고리 ID"
        VARCHAR name UK "카테고리 이름"
    }

    DICTIONARY {
        BIGINT id PK "사전 항목 ID"
        BIGINT category_id FK "카테고리 ID"
        VARCHAR question "사전 질문·표제어"
        TEXT content "사전 설명"
        BOOLEAN deleted "삭제 여부"
        TIMESTAMP deleted_at "삭제 일시"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }

    DICTIONARY_CATEGORY ||--o{ DICTIONARY : "사전 항목 분류"
```

## 4. 독립 테이블

피드백과 추천 일정은 다른 PostgreSQL 테이블과 FK 관계가 없습니다.

```mermaid
erDiagram
    FEEDBACK {
        BIGINT id PK "피드백 ID"
        VARCHAR type "피드백 유형"
        VARCHAR content "피드백 내용"
        VARCHAR email "답변받을 이메일"
        INTEGER rating "평점"
        VARCHAR calculation_id "연차 계산 결과 식별자"
        VARCHAR platform "요청 플랫폼"
        VARCHAR status "처리 상태"
        TIMESTAMP created_at "생성 일시"
    }

    RECOMMENDED_SCHEDULES {
        BIGINT id PK "추천 일정 ID"
        VARCHAR name "추천 일정 이름"
        DATE start_date "시작일"
        DATE end_date "종료일"
        TIMESTAMP created_at "생성 일시"
        TIMESTAMP updated_at "수정 일시"
    }
```

## 핵심 구조 설명

### 사용자와 연차 정책

- `users`: 회원 계정과 로그인 정보를 저장합니다.
- `user_leave_policies`: 입사일, 연차 산정 기준, 근무 형태를 저장합니다.
- 사용자 한 명당 연차 정책은 최대 한 개입니다.
- `totalLeave`, `usedLeave` 일수는 DB에 저장하지 않고 분으로 환산합니다.

### 연차 잔액과 발생 원장

- `leave_yearly_balances`: 사용 기간별 총 연차와 사용 연차를 분 단위로 저장합니다.
- `leave_grants`: 최초 연차, 월차, 비례 연차, 정기 연차의 발생 내역과 유효기간을 저장합니다.
- 남은 연차는 별도 컬럼이 아니라 다음처럼 계산합니다.

```text
잔액 기준 남은 연차 = total_leave_minutes - used_leave_minutes
원장 기준 남은 연차 = granted_minutes + adjusted_minutes - used_minutes
```

### 일정과 연차 차감

- `calendar_events`: 일반 일정과 연차 일정을 함께 저장합니다.
- `calendar_event_leave_allocations`: 일정이 어느 연차 발생분에서 몇 분 차감됐는지 기록합니다.
- 하나의 일정이 여러 연차 발생 원장에 나뉘어 연결될 수 있습니다.

## 주요 제약조건

| 대상 | 제약조건 |
| --- | --- |
| 사용자 | 이메일은 중복될 수 없음 |
| 연차 잔액 | 사용자와 시작일·종료일 조합은 중복될 수 없음 |
| 연차 발생 원장 | 사용자와 `source_key` 조합은 중복될 수 없음 |
| 연차 발생 원장 | 시작일은 종료일보다 늦을 수 없음 |
| 연차 발생 원장 | 발생·조정·사용량 계산 결과가 음수가 될 수 없음 |
| 일정 차감 | `allocated_minutes`는 0보다 커야 함 |
| 알림 | 사용자와 `event_key` 조합은 중복될 수 없음 |
| 사전 카테고리 | 카테고리 이름은 중복될 수 없음 |
| 추천 일정 | 이름·시작일·종료일이 모두 같은 일정은 중복될 수 없음 |

## PostgreSQL 외 저장소

| DynamoDB 테이블 | 용도 |
| --- | --- |
| `holidays` | 연도별 공휴일 정보 |
| `app_version_policy` | 플랫폼별 현재·최소 앱 버전과 업데이트 정보 |
