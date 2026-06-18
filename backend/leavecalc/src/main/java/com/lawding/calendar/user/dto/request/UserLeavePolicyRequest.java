package com.lawding.calendar.user.dto.request;

import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import java.time.LocalDate;

public record UserLeavePolicyRequest(
    String nickname,
    Boolean acceptedTerms, // 약관 동의
    LeaveAccrualBasis leaveAccrualBasis, // 연차 산정 기준 (HIRE_DATE, FISCAL_YEAR)
    Integer fiscalYearBaseMonth, // 회계연도 시작 월 (회계연도를 선택한 경우에만 옴)
    LocalDate joinDate, // 입사일 (근무일)
    WorkPattern workPattern, // 요일별 근무 패턴 정보
    Integer companySize, // 상시 근로자 수
    Integer totalLeave, // 총 발생한 연차 개수
    Integer usedLeave // 사용한 연차 개수
) {

}
