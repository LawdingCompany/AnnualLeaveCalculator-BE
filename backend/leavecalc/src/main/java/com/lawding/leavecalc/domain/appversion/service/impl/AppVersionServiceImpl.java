package com.lawding.leavecalc.domain.appversion.service.impl;

import com.lawding.leavecalc.domain.appversion.dto.AppVersionInfo;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.AppVersionRequest;
import com.lawding.leavecalc.domain.appversion.model.AppVersion;
import com.lawding.leavecalc.domain.appversion.repository.AppVersionRepository;
import com.lawding.leavecalc.domain.appversion.service.AppVersionService;
import com.lawding.leavecalc.domain.appversion.util.VersionComparator;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppVersionServiceImpl implements AppVersionService {

    private final AppVersionRepository repository;


    @Override
    public AppVersionInfo checkVersion(Platform platform, String clientVersion) {
        AppVersion policy = findPolicyOrThrow(platform);

        boolean forceUpdate = VersionComparator.isLower(clientVersion, policy.getMinimumVersion());

        return AppVersionInfo.from(policy, forceUpdate);
    }

    @Override
    public List<AppVersion> findAllAppVersion() {
        return repository.scanAll();
    }

    @Override
    public AppVersion findAppVersion(Platform platform) {
        return findPolicyOrThrow(platform);
    }

    @Override
    public void createAppVersionPolicy(AppVersionRequest request) {
        AppVersion policy = AppVersion.create(
            request.platform().getValue(),
            request.currentVersion(),
            request.minimumVersion(),
            request.updateMessage(),
            request.downloadUrl());

        repository.save(policy);
    }

    @Override
    public void updateCurrentVersion(Platform platform, String currentVersion) {
        AppVersion policy = findPolicyOrThrow(platform);
        policy.changeCurrentVersion(currentVersion);
        repository.save(policy);
    }

    @Override
    public void updateMinimumVersion(Platform platform, String minimumVersion) {
        AppVersion policy = findPolicyOrThrow(platform);
        policy.changeMinimumVersion(minimumVersion);
        repository.save(policy);
    }

    @Override
    public void updateUpdateMessage(Platform platform, String updateMessage) {
        AppVersion policy = findPolicyOrThrow(platform);
        policy.changeUpdateMessage(updateMessage);
        repository.save(policy);
    }

    @Override
    public void updateDownloadUrl(Platform platform, String downloadUrl) {
        AppVersion policy = findPolicyOrThrow(platform);
        policy.changeDownloadUrl(downloadUrl);
        repository.save(policy);
    }

    @Override
    public void deleteAppVersionPolicy(Platform platform) {
        repository.delete(platform.getValue());
    }


    private AppVersion findPolicyOrThrow(Platform platform) {
        return repository.findByPlatform(platform.getValue())
            .orElseThrow(() ->
                new IllegalStateException(
                    "해당 플랫폼 정책이 존재하지 않습니다. platform=" + platform.getValue()
                )
            );
    }

}
