package com.lawding.leavecalc.domain.appversion.controller.admin;

import com.lawding.leavecalc.domain.appversion.dto.request.admin.AppVersionRequest;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.UpdateCurrentVersionRequest;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.UpdateDownloadUrlRequest;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.UpdateMinimumVersionRequest;
import com.lawding.leavecalc.domain.appversion.dto.request.admin.UpdateUpdateMessageRequest;
import com.lawding.leavecalc.domain.appversion.dto.response.admin.AppVersionAdminResponse;
import com.lawding.leavecalc.domain.appversion.service.AppVersionService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/app-versions")
public class AppVersionAdminController {

    private final AppVersionService appVersionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppVersionAdminResponse>>> getAllAppVersion() {
        log.info("GET /admin/app-versions - 모든 앱 정책 정보 요청");
        return ResponseEntity.ok(
            ApiResponse.ok(
                appVersionService.findAllAppVersion()
                    .stream()
                    .map(AppVersionAdminResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping("{platform}")
    public ResponseEntity<ApiResponse<AppVersionAdminResponse>> getAppVersion(
        @PathVariable Platform platform) {
        log.info("GET /admin/app-versions/{} - {} 정책 정보 요청", platform.getValue(),
            platform.getValue());
        return ResponseEntity.ok(
            ApiResponse.ok(
                AppVersionAdminResponse.from(
                    appVersionService.findAppVersion(platform)
                )
            )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAppVersion(
        @Valid @RequestBody AppVersionRequest request
    ) {
        log.info("POST /admin/app-versions - {} 정책 등록/수정 : {}", request.platform().getValue(),
            request);
        appVersionService.createAppVersionPolicy(request);
        return ResponseEntity.ok(ApiResponse.okMessage("애플리케이션 버전이 등록되었습니다."));
    }

    @PatchMapping("/{platform}/current-version")
    public ResponseEntity<ApiResponse<Void>> updateCurrentVersion(
        @PathVariable Platform platform,
        @Valid @RequestBody UpdateCurrentVersionRequest request
    ) {
        log.info("PATCH /admin/app-versions/{}/current-version - {} 최신 버전 수정 : {}",
            platform.getValue(), platform.getValue(), request.currentVersion());

        appVersionService.updateCurrentVersion(platform, request.currentVersion());

        return ResponseEntity.ok(ApiResponse.okMessage("애플리케이션 최신 버전이 수정되었습니다."));
    }

    @PatchMapping("/{platform}/minimum-version")
    public ResponseEntity<ApiResponse<Void>> updateMinimumVersion(
        @PathVariable Platform platform,
        @Valid @RequestBody UpdateMinimumVersionRequest request
    ) {
        log.info("PATCH /admin/app-versions/{}/minimum-version - {} 최소 버전 수정 : {}",
            platform.getValue(), platform.getValue(), request.minimumVersion());

        appVersionService.updateMinimumVersion(platform, request.minimumVersion());

        return ResponseEntity.ok(ApiResponse.okMessage("애플리케이션 최소 버전이 수정되었습니다."));
    }

    @PatchMapping("/{platform}/update-message")
    public ResponseEntity<ApiResponse<Void>> updateUpdateMessage(
        @PathVariable Platform platform,
        @Valid @RequestBody UpdateUpdateMessageRequest request
    ) {
        log.info("PATCH /admin/app-versions/{}/update-message - {} 업데이트 메시지 수정 : {}",
            platform.getValue(), platform.getValue(), request.updateMessage());

        appVersionService.updateUpdateMessage(platform, request.updateMessage());

        return ResponseEntity.ok(ApiResponse.okMessage("애플리케이션 업데이트 메시지가 수정되었습니다."));
    }

    @PatchMapping("/{platform}/download-url")
    public ResponseEntity<ApiResponse<Void>> updateDownloadUrl(
        @PathVariable Platform platform,
        @Valid @RequestBody UpdateDownloadUrlRequest request
    ) {
        log.info("PATCH /admin/app-versions/{}/download-url - {} 다운로드 url 수정 : {}",
            platform.getValue(), platform.getValue(), request.downloadUrl());

        appVersionService.updateDownloadUrl(platform, request.downloadUrl());

        return ResponseEntity.ok(ApiResponse.okMessage("애플리케이션 Download URL이 수정되었습니다."));
    }

    @DeleteMapping("/{platform}")
    public ResponseEntity<ApiResponse<Void>> deleteAppVersion(
        @PathVariable Platform platform
    ) {
        log.info("DELETE /admin/app-versions/{} - {} 정책 삭제", platform.getValue(),
            platform.getValue());
        appVersionService.deleteAppVersionPolicy(platform);
        return ResponseEntity.ok(ApiResponse.okMessage("해당 앱 정책이 수정되었습니다."));
    }
}
