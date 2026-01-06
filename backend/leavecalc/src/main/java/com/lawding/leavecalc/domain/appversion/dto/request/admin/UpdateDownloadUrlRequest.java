package com.lawding.leavecalc.domain.appversion.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UpdateDownloadUrlRequest(
    @NotBlank
    @URL
    String downloadUrl
) {

}
