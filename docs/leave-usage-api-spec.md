# 연차 사용 유형 API 명세 (기존 경로 확장)

이 문서는 기존 경로를 유지하면서 사용자 연차 정책에 사용 유형을 추가한 구현 명세입니다. 새 `/v2` 경로는 만들지 않습니다. 일정 API에는 유형 필드를 추가하지 않습니다. 로그인 사용자 정책을 백엔드에서 조회해 검증합니다.

## 공통 규칙

- 유형 코드: `QUARTER_DAY`(반반차, 0.25일), `HALF_DAY`(반차, 0.5일), `FULL_DAY`(일, 1일), `HOURLY`(시간).
- 요청 필드명과 응답 필드명은 `leaveUsageType`입니다.
- 기존 사용자를 위해 nullable이며 최초 등록에서 생략하거나 `null`을 보내도 정상 처리합니다. 이 경우 DB에는 NULL로 저장합니다. 명시적으로 `HOURLY`를 보내면 `HOURLY`로 저장합니다. 저장된 NULL은 백엔드에서 시간 유형처럼 처리합니다. 정책 수정에서 필드가 생략되거나 `null`이면 기존 유형을 보존합니다.
- 유형이 NULL인 사용자와 명시적 `HOURLY` 사용자는 동일한 시간 유형 규칙을 적용합니다. 양의 정수 분 단위로 신청하고 잔액을 초과할 수 없습니다.
- 이 명세는 이번 구현의 API 계약입니다.

## 1. 사용자 연차 정책 조회

`GET /users/leave-policy`

응답의 기존 `data` 객체에 `leaveUsageType`을 추가합니다.

```json
{
  "data": {
    "userId": 123,
    "acceptedAt": "2026-10-17T10:00:00",
    "leaveAccrualBasis": 1,
    "hireDate": "2024-03-01",
    "fiscalYearBaseMonth": 1,
    "companySize": 30,
    "workPattern": {},
    "breakTimePattern": {},
    "leaveUsageType": "QUARTER_DAY"
  }
}
```

유형이 아직 설정되지 않았다면 `"leaveUsageType": null`을 반환합니다. 이 사용자는 백엔드에서 시간 유형 규칙으로 처리합니다.

## 2. 사용자 연차 정책 최초 등록

`POST /users/leave-policy`

기존 요청 필드에 선택 필드 `leaveUsageType`을 추가합니다. 구버전 앱처럼 필드를 생략한 요청도 허용하며, 이 경우 NULL로 저장합니다.

```json
{
  "leaveAccrualBasis": 1,
  "fiscalYearBaseMonth": 1,
  "hireDate": "2024-03-01",
  "companySize": 30,
  "workPattern": {},
  "breakTimePattern": {},
  "totalLeave": 15,
  "usedLeave": 0,
  "leaveUsageType": "QUARTER_DAY"
}
```

현재 POST는 성공 메시지만 반환하므로 응답 형식은 바꾸지 않습니다.

## 3. 사용자 연차 정책 수정

`PUT /users/leave-policy`

기존 요청/응답을 유지하고 요청 및 응답에 `leaveUsageType`을 추가합니다. 기존 앱이 해당 필드를 보내지 않을 수 있으므로, 수정 시 필드 누락 또는 `null`로 기존 유형을 덮어쓰지 않습니다. 새 앱은 유형을 변경할 때 코드값을 명시합니다. 이 계약에서는 API로 유형을 NULL로 초기화하지 않습니다.

```json
{
  "leaveAccrualBasis": 1,
  "fiscalYearBaseMonth": 1,
  "hireDate": "2024-03-01",
  "companySize": 30,
  "workPattern": {},
  "breakTimePattern": {},
  "totalLeave": 15,
  "usedLeave": 1.25,
  "leaveUsageType": "HALF_DAY"
}
```

응답은 `GET /users/leave-policy`와 같은 `UserLeavePolicyResponse` 구조입니다.

## 4. 통합 사용자 정보 조회

`GET /users/me`

기존 응답의 `leavePolicy` 객체에 `leaveUsageType`을 추가합니다. 별도 최상위 필드는 추가하지 않습니다.

```json
{
  "data": {
    "user": {},
    "leavePolicy": {
      "userId": 123,
      "leaveUsageType": "HOURLY"
    },
    "leaveBalance": {}
  }
}
```

정책이 없으면 기존 동작에 따라 `leavePolicy: null`입니다. 정책은 있지만 유형이 설정되지 않았다면 `leaveUsageType: null`이고, 이 사용자는 백엔드에서 시간 유형 규칙으로 처리합니다.

## 5. 일정 등록/수정 API

기존 일정 경로와 요청/응답 형태를 유지합니다. 요청에 `leaveUsageType`은 추가하지 않습니다. 기존 `usedLeaveMinutes`를 백엔드가 로그인 사용자의 `user_leave_policies.leave_usage_type`과 대조합니다.

- 반반차/반차/일 유형: 사용자 평균 근로시간으로 단위를 분으로 환산하고, 신청 분이 해당 단위의 배수인지 검증합니다. 한 번에 여러 단위 신청은 허용합니다.
- 시간 유형: 양의 정수 분이면 허용합니다. 잔액 초과와 0분 신청은 거부합니다.
- 일 유형 사용자의 잔액이 0이면 거부합니다. 잔액은 양수지만 최소 단위보다 작을 때 마지막 최소 단위 신청을 허용하고 잔액을 0으로 만듭니다.
- 유형이 NULL인 사용자와 `HOURLY` 사용자는 동일하게 처리합니다: 양의 정수 분 단위, 잔액 이내 신청만 허용합니다. 필드가 없는 구버전 앱 요청도 이 규칙으로 정상 처리합니다.
- 일정 수정에서도 기존 사용량을 원복한 뒤 새 요청 기준으로 같은 정책 검증을 수행해야 합니다.

유형 단위 불일치는 `LEAVE_MINUTES_INVALID`, 잔액 초과는 `LEAVE_BALANCE_NOT_ENOUGH` 오류를 반환합니다.

### 마지막 잔액 예시와 저장 정합성

하루 8시간인 사용자에게 반반차 단위는 120분입니다. 잔액이 48분일 때 120분 사용 신청을 허용하면 일정의 `usedLeaveMinutes`는 신청량 120분을 보존하고, `calendar_event_leave_allocations.allocated_minutes`에는 실제 차감량 48분을 기록합니다. 현재 스키마에 이미 이 분리 저장 구조가 있으므로 일정 API 계약이나 테이블에 별도 신청량 필드를 추가하지 않습니다. 백엔드는 신청량이 양의 잔액보다 한 단위 내에서만 초과하도록 검증한 뒤, 신청량과 잔액 중 작은 값만 원장에 실제 배분합니다.

## 6. 삭제

`DELETE /users/leave-policy`는 요청/응답 형식을 변경하지 않습니다. 삭제 후 정책이 없는 경우 기존 호환 동작을 적용합니다.
