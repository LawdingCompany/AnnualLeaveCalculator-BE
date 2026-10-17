# 연차 사용 유형 DB 수동 변경 가이드

## 저장 위치와 호환성

사용자의 사용 유형은 `user_leave_policies.leave_usage_type` 한 곳에만 저장합니다. 일정 API 요청에 유형을 추가하지 않고, 일정 등록·수정 때 백엔드가 인증된 사용자 ID로 정책을 조회해 검증합니다.

기존 회원과 구버전 앱 호환을 위해 우선 NULL 허용입니다. NULL은 DB에 미설정 상태로 저장하지만, 백엔드는 이를 시간 유형처럼 처리합니다. 구버전 요청에서 필드가 생략되어도 정상 처리되며 분 단위와 잔액 이내 규칙을 적용합니다. 명시적으로 `HOURLY`를 보내면 그 코드값을 저장합니다. 과거 잔액과 사용내역은 변환하지 않습니다.

## DB 변경 SQL (PostgreSQL)

Flyway는 사용하지 않습니다. Hibernate `ddl-auto: validate`는 스키마를 생성·수정하지 않고 매핑된 테이블/컬럼만 확인합니다. DB 변경을 새 백엔드 배포보다 먼저 적용하세요. 운영 DB 변경 전 백업하고 동일 PostgreSQL 버전의 개발/스테이징에서 확인하세요.

```sql
BEGIN;

ALTER TABLE user_leave_policies
    ADD COLUMN leave_usage_type VARCHAR(20) NULL;

ALTER TABLE user_leave_policies
    ADD CONSTRAINT ck_user_leave_policies_leave_usage_type
    CHECK (leave_usage_type IS NULL OR leave_usage_type IN
        ('QUARTER_DAY', 'HALF_DAY', 'FULL_DAY', 'HOURLY'));

COMMIT;
```

기존 행의 값은 NULL로 유지됩니다.

## 코드와 규칙

| 저장 코드 | 의미 | 허용 신청량 |
|---|---|---|
| `QUARTER_DAY` | 반반차 | 0.25일의 배수 |
| `HALF_DAY` | 반차 | 0.5일의 배수 |
| `FULL_DAY` | 연차 | 1일의 배수 |
| `HOURLY` | 시간 | 양의 정수 분 단위, 잔액 이내 |

날짜 단위는 사용자의 평균 근로시간으로 분 환산합니다. 하루 7시간이면 반반차 105분, 반차 210분, 하루 420분입니다. 시간 유형은 30분도 허용하고 최소 신청 간격을 두지 않습니다. 날짜 단위는 여러 단위를 한 번에 신청할 수 있어 1일 초과를 허용합니다.

잔액이 0이면 신청을 거부합니다. 날짜 유형에서 잔액이 양수지만 선택한 최소 단위보다 작으면 마지막 최소 단위 신청을 허용하고 잔액을 0으로 만듭니다. 시간 유형은 잔액을 초과할 수 없습니다.

## 이벤트 테이블과 마지막 잔액 처리

이번 설계에서는 이벤트 테이블에 사용 유형을 복제하지 않으며 이벤트 API 계약도 바꾸지 않습니다. 현재 스키마는 `calendar_events.used_leave_minutes`에 신청량을, `calendar_event_leave_allocations.allocated_minutes`에 실제 차감량을 기록하므로 둘을 분리할 수 있습니다. 예를 들어 48분 잔액의 반반차 사용자가 120분을 신청하면 일정에는 120분, 원장 배분에는 실제 차감 48분을 기록합니다.

백엔드는 날짜 유형의 마지막 소진 규칙을 적용해 신청량과 양의 잔액 중 작은 값을 원장에 실제 배분합니다. NULL 유형과 시간 유형은 신청량 전체가 잔액 이내일 때만 배분합니다.

`CalendarEvent`에서 사용하지 않는 `requested_leave_minutes`, `leave_usage_type` 매핑과 `recordLeaveUsage`를 제거했습니다. DB에는 위 SQL의 정책 유형 컬럼 외에 이벤트 컬럼을 추가하지 않습니다.

## 적용 결과 확인

```sql
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'user_leave_policies'
  AND column_name = 'leave_usage_type';

SELECT conname
FROM pg_constraint
WHERE conname = 'ck_user_leave_policies_leave_usage_type';
```

## 롤백 SQL

새 백엔드가 유형 컬럼을 더 이상 읽지 않는 상태인지 확인한 뒤 실행합니다.

```sql
ALTER TABLE user_leave_policies
    DROP CONSTRAINT ck_user_leave_policies_leave_usage_type;
ALTER TABLE user_leave_policies
    DROP COLUMN leave_usage_type;
```
