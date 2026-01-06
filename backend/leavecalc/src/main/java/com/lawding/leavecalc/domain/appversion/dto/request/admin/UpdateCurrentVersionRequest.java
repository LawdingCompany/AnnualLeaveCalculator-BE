package com.lawding.leavecalc.domain.appversion.dto.request.admin;

import jakarta.validation.constraints.Pattern;

public record UpdateCurrentVersionRequest(
    @Pattern(
        regexp = "^\\d+\\.\\d+\\.\\d+$",
        message = "currentVersion X.X.X 형식이어야 합니다"
    )
    String currentVersion
) {

}
