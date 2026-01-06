package com.lawding.leavecalc.domain.appversion.service;

import com.lawding.leavecalc.domain.appversion.dto.AppVersionInfo;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.AppVersionRequest;
import com.lawding.leavecalc.domain.appversion.model.AppVersion;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import java.util.List;

public interface AppVersionService {

    AppVersionInfo checkVersion(Platform platform, String clientVersion);

    List<AppVersion> findAllAppVersion();

    AppVersion findAppVersion(Platform platform);

    void createAppVersionPolicy(AppVersionRequest request);

    void updateCurrentVersion(Platform platform, String currentVersion);

    void updateMinimumVersion(Platform platform, String minimumVersion);

    void updateUpdateMessage(Platform platform, String updateMessage);

    void updateDownloadUrl(Platform platform, String downloadUrl);

    void deleteAppVersionPolicy(Platform platform);
}
