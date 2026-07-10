package com.sree.swingengine.market.session;

import com.sree.swingengine.config.AngelProperties;
import com.sree.swingengine.market.client.angel.dto.AngelLoginRequest;
import com.sree.swingengine.market.client.angel.dto.AngelLoginResponse;
import com.sree.swingengine.util.NetworkUtil;
import com.sree.swingengine.util.TotpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AngelSessionManager {

    private final RestClient restClient;
    private final AngelProperties properties;

    private String jwtToken;

    public String getJwtToken() {

        if (jwtToken == null) {

            log.info("Creating new Angel session...");

            AngelLoginResponse response = login();

            if (!response.getStatus()) {
                throw new RuntimeException("Angel login failed");
            }

            jwtToken = response.getData().getJwtToken();

            log.info("Angel session created successfully.");
        }

        return jwtToken;
    }

    private AngelLoginResponse login() {
        log.info("Logging in...");

        AngelLoginRequest request = AngelLoginRequest.builder()
                .clientcode(properties.getClientCode())
                .password(properties.getPin())
                .totp(String.valueOf(TotpGenerator.generate(properties.getTotpSecret())))
                .build();

        return restClient.post()
                .uri(properties.getBaseUrl() + properties.getLoginEndpoint())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-UserType", "USER")
                .header("X-SourceID", "WEB")
                .header("X-ClientLocalIP", NetworkUtil.getLocalIp())
                .header("X-ClientPublicIP", properties.getClientPublicIp())
                .header("X-MACAddress", NetworkUtil.getMacAddress())
                .header("X-PrivateKey", properties.getApiKey())
                .body(request)
                .retrieve()
                .body(AngelLoginResponse.class);
    }

    public void clearSession() {
        jwtToken = null;
    }
}