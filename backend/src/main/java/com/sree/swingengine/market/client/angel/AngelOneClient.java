package com.sree.swingengine.market.client.angel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sree.swingengine.config.AngelProperties;
import com.sree.swingengine.market.client.angel.dto.*;
import com.sree.swingengine.market.session.AngelSessionManager;
import com.sree.swingengine.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AngelOneClient {

    private final RestClient restClient;
    private final AngelProperties properties;
    private final ObjectMapper objectMapper;
    private final AngelSessionManager angelSessionManager;

    public List<AngelInstrumentDto> getInstruments() throws JsonProcessingException {

        String json = downloadInstrumentMaster();

        return objectMapper.readValue(
                json,
                new TypeReference<List<AngelInstrumentDto>>() {
                });
    }
    public String downloadInstrumentMaster() {

        return restClient.get()
                .uri(properties.getInstrumentMasterEndpoint()).retrieve()
                .body(String.class);
    }
    public AngelHistoricalCandleResponse downloadHistoricalCandles(
            String symbolToken,
            LocalDate from,
            LocalDate to) {

        String jwt = angelSessionManager.getJwtToken();

        AngelHistoricalCandleRequest request =
                AngelHistoricalCandleRequest.builder()
                        .exchange("NSE")
                        .symboltoken(symbolToken)
                        .interval("ONE_DAY")
                        .fromdate(from.atStartOfDay()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .todate(to.atTime(23, 59)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .build();

        return restClient.post()
                .uri(properties.getBaseUrl() + properties.getHistoricalCandleEndpoint())
                .header("Authorization", "Bearer " + jwt)
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
                .body(AngelHistoricalCandleResponse.class);
    }

}