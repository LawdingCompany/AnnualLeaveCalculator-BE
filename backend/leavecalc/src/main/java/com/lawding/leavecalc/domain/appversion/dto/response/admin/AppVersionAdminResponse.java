package com.lawding.leavecalc.domain.appversion.dto.response.admin;

import com.lawding.leavecalc.domain.appversion.model.AppVersion;

public record AppVersionAdminResponse(
    String platform,
    String currentVersion,
    String minimumVersion,
    String updateMessage,
    String downloadUrl
) {

    public static AppVersionAdminResponse from(AppVersion appVersion) {
        return new AppVersionAdminResponse(
            appVersion.getPlatform(),
            appVersion.getCurrentVersion(),
            appVersion.getMinimumVersion(),
            appVersion.getUpdateMessage(),
            appVersion.getDownloadUrl()
        );
    }
}
