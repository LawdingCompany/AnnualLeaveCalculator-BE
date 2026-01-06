package com.lawding.leavecalc.domain.appversion.dto.response;

import com.lawding.leavecalc.domain.appversion.dto.AppVersionInfo;

public record AppVersionResponse(
    String platform,
    String currentVersion,
    String minimumVersion,
    boolean forceUpdate,
    String updateMessage,
    String downloadUrl
) {
    public static AppVersionResponse from(AppVersionInfo appVersion) {
        return new AppVersionResponse(
            appVersion.platform(),
            appVersion.currentVersion(),
            appVersion.minimumVersion(),
            appVersion.forceUpdate(),
            appVersion.updateMessage(),
            appVersion.downloadUrl()
        );
    }
}
