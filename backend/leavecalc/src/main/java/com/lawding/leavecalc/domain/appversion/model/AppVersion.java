package com.lawding.leavecalc.domain.appversion.model;

import com.lawding.leavecalc.domain.appversion.util.VersionComparator;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@DynamoDbBean
public class AppVersion {

    private String platform; // iOS, Android
    private String currentVersion;
    private String minimumVersion;
    private String updateMessage;
    private String downloadUrl;
    private String updatedAt;

    @DynamoDbPartitionKey
    public String getPlatform() {
        return platform;
    }

    private String nowKst() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static AppVersion create(String platform, String currentVersion, String minimumVersion,
        String updateMessage, String downloadUrl) {
        AppVersion appVersion = new AppVersion();
        appVersion.platform = platform;
        appVersion.currentVersion = currentVersion;
        appVersion.changeMinimumVersion(minimumVersion);
        appVersion.updateMessage = updateMessage;
        appVersion.downloadUrl = downloadUrl;
        appVersion.updatedAt = appVersion.nowKst();
        return appVersion;
    }

    public void changeCurrentVersion(String version) {
        this.currentVersion = version;
        this.updatedAt = nowKst();
    }

    public void changeMinimumVersion(String version) {
        if (VersionComparator.compare(version, this.currentVersion) > 0) {
            throw new IllegalArgumentException(
                "minimumVersion(" + version +
                ")은 currentVersion(" + this.currentVersion +
                ")보다 클 수 없습니다."
            );
        }
        this.minimumVersion = version;
        this.updatedAt = nowKst();
    }

    public void changeUpdateMessage(String message) {
        this.updateMessage = message;
        this.updatedAt = nowKst();
    }

    public void changeDownloadUrl(String url) {
        this.downloadUrl = url;
        this.updatedAt = nowKst();
    }

}
