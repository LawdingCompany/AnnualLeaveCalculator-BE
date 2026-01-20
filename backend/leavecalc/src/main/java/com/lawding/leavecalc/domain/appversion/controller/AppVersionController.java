package com.lawding.leavecalc.domain.appversion.controller;

import com.lawding.leavecalc.domain.appversion.dto.response.AppVersionResponse;
import com.lawding.leavecalc.domain.appversion.service.AppVersionService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.dto.response.ApiResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/app/version-check")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping
    public ResponseEntity<ApiResponse<AppVersionResponse>> checkAppVersion(
        @RequestParam Platform platform,
        @RequestParam("client-version")
        @Pattern(
            regexp = "^\\d+\\.\\d+\\.\\d+$",
            message = "client-version은 X.X.X 형식이어야 합니다"
        ) String clientVersion
    ) {
        log.info("[req] GET /app/version-check?platform=[]&client-version=[] - 앱버전 체크 요청");

        return ResponseEntity.ok(
            ApiResponse.ok(
                AppVersionResponse.from(appVersionService.checkVersion(platform, clientVersion))
            )
        );
    }
}
