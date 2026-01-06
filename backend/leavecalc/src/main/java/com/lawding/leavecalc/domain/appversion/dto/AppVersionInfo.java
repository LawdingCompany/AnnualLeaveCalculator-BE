package com.lawding.leavecalc.domain.appversion.dto;

import com.lawding.leavecalc.domain.appversion.model.AppVersion;

public record AppVersionInfo(
    String platform,
    String currentVersion,
    String minimumVersion,
    boolean forceUpdate,
    String updateMessage,
    String downloadUrl
){
    public static AppVersionInfo from(AppVersion appVersion, boolean forceUpdate) {
        return new AppVersionInfo(
            appVersion.getPlatform(),
            appVersion.getCurrentVersion(),
            appVersion.getMinimumVersion(),
            forceUpdate,
            appVersion.getUpdateMessage(),
            appVersion.getDownloadUrl()
        );
    }
}
