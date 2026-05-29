package com.lawding.leavecalc.domain.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {
    private String teamId;
    private String clientId;
    private String keyId;
    private String privateKey;
    private String redirectUri;
}
