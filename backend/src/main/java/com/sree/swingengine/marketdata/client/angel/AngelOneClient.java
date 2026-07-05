package com.sree.swingengine.marketdata.client.angel;

import com.sree.swingengine.config.AngelProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AngelOneClient {

    private final RestClient restClient;
    private final AngelProperties properties;

}