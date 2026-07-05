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

    private String apiKey;
    private String clientCode;
    private String pin;
    private String totpSecret;

}