package com.lawding.calendar.user.enums;

import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;

public enum LeaveAccrualBasis {
    HIRE_DATE(1),
    FISCAL_YEAR(2);

    private final int code;

    LeaveAccrualBasis(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static LeaveAccrualBasis fromCode(Integer code) {
        if (code == null) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "leaveAccrualBasis는 필수입니다.");
        }

        for (LeaveAccrualBasis basis : values()) {
            if (basis.code == code) {
                return basis;
            }
        }

        throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "leaveAccrualBasis는 1(HIRE_DATE) 또는 2(FISCAL_YEAR)여야 합니다.");
    }
}
