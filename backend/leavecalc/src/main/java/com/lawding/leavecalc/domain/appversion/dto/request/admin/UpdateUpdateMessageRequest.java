package com.lawding.leavecalc.domain.appversion.dto.request.admin;

import jakarta.validation.constraints.Size;

public record UpdateUpdateMessageRequest(
    @Size(max = 100, message = "메시지는 100자 이내로 작성 가능합니다.")
    String updateMessage
) {

}
