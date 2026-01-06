package com.lawding.leavecalc.domain.appversion.dto.request.admin;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record AppVersionRequest(
    @NotNull
    Platform platform,
    @NotBlank
    @Pattern(
        regexp = "^\\d+\\.\\d+\\.\\d+$",
        message = "currentVersion은 X.X.X 형식이어야 합니다"
    )
    String currentVersion,

    @NotBlank
    @Pattern(
        regexp = "^\\d+\\.\\d+\\.\\d+$",
        message = "minimumVersion은 X.X.X 형식이어야 합니다"
    )
    String minimumVersion,
    @Size(max = 100, message = "메시지는 100자 이내로 작성 가능합니다.")
    String updateMessage,
    @NotBlank
    @URL
    String downloadUrl
) {

}
