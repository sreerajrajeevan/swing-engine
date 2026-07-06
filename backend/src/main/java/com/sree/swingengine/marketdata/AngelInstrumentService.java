package com.sree.swingengine.marketdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.marketdata.client.angel.AngelOneClient;
import com.sree.swingengine.marketdata.client.angel.dto.AngelInstrumentDto;
import com.sree.swingengine.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AngelInstrumentService {

    private final AngelOneClient angelOneClient;
    private final ObjectMapper objectMapper;
    private final StockRepository stockRepository;

    public List<AngelInstrumentDto> getNseEquities() throws JsonProcessingException {

        String json = angelOneClient.downloadInstrumentMaster();

        List<AngelInstrumentDto> instruments = objectMapper.readValue(
                json,
                new TypeReference<List<AngelInstrumentDto>>() {
                });

        log.info("Total instruments: {}", instruments.size());

        return instruments.stream()
                .filter(i -> "NSE".equals(i.getExch_seg()))
                .filter(i -> i.getSymbol() != null)
                .filter(i -> i.getSymbol().endsWith("-EQ"))
                .toList();
    }

    public Map<String, String> getTokenMap() throws JsonProcessingException {

        return getNseEquities().stream()
                .collect(Collectors.toMap(
                        instrument -> instrument.getSymbol().replace("-EQ", ""),
                        AngelInstrumentDto::getToken,
                        (existing, replacement) -> existing
                ));
    }

    @Transactional
    public void syncSymbolTokens() throws JsonProcessingException {

        Map<String, String> tokenMap = getTokenMap();

        List<Stock> stocks = stockRepository.findAll();

        int updated = 0;

        for (Stock stock : stocks) {

            String token = tokenMap.get(stock.getSymbol());

            if (token != null) {
                stock.setSymbolToken(token);
                updated++;
            }
        }

        stockRepository.saveAll(stocks);

        log.info("Updated {} stocks with symbol tokens", updated);
    }
}