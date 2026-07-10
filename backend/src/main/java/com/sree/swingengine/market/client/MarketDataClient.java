package com.sree.swingengine.market.client;

import com.sree.swingengine.config.MarketDataProperties;
import com.sree.swingengine.market.dto.StockMasterDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MarketDataClient {

    private final RestClient restClient;
    private final MarketDataProperties properties;

    public MarketDataClient(RestClient restClient,
                            MarketDataProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public List<StockMasterDto> downloadNifty500(){

        String csv = restClient
                .get()
                .uri(properties.getNifty500Url())
                .retrieve()
                .body(String.class);

        return parseCsv(csv);

    }
    private List<StockMasterDto> parseCsv(String csv) {

        List<StockMasterDto> stocks = new ArrayList<>();

        try (
                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(new StringReader(csv))
        ) {

            for (CSVRecord record : parser) {

                StockMasterDto dto = StockMasterDto.builder()
                        .symbol(record.get("Symbol"))
                        .companyName(record.get("Company Name"))
                        .industry(record.get("Industry"))
                        .isinCode(record.get("ISIN Code"))
                        .build();

                stocks.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Nifty500 CSV", e);
        }

        return stocks;
    }
}