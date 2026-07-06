package com.sree.swingengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "angel")
public class AngelProperties {

    private String baseUrl;
    private String loginEndpoint;
    private String historicalCandleEndpoint;

    private String apiKey;
    private String clientCode;
    private String pin;
    private String totpSecret;

    private String clientPublicIp;
}