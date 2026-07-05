package com.sree.swingengine.marketdata;

import com.sree.swingengine.dto.StockMasterDto;
import com.sree.swingengine.entity.Stock;
import com.sree.swingengine.marketdata.client.MarketDataClient;
import com.sree.swingengine.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MasterStockService {

    private static final Logger log =
            LoggerFactory.getLogger(MasterStockService.class);
    private final MarketDataClient marketDataClient;
    private final StockRepository stockRepository;

    public MasterStockService(MarketDataClient marketDataClient, StockRepository stockRepository) {
        this.marketDataClient = marketDataClient;
        this.stockRepository = stockRepository;
    }
    public void loadNifty500() {

        List<StockMasterDto> stocks = marketDataClient.downloadNifty500();

        log.info("Downloaded {} stocks", stocks.size());

        for (StockMasterDto dto : stocks) {
            saveOrUpdate(dto);
        }

        log.info("Successfully imported {} stocks.", stocks.size());

    }
    private void saveOrUpdate(StockMasterDto dto) {

        Optional<Stock> existingStock = stockRepository.findBySymbol(dto.getSymbol());

        if (existingStock.isPresent()) {

            Stock stock = existingStock.get();

            stock.setCompanyName(dto.getCompanyName());
            stock.setIndustry(dto.getIndustry());
            stock.setIsinCode(dto.getIsinCode());
            stock.setNifty500(true);
            stock.setActive(true);

            stockRepository.save(stock);

        } else {

            Stock stock = toEntity(dto);

            stockRepository.save(stock);
        }

    }
    private Stock toEntity(StockMasterDto dto) {

        return Stock.builder()
                .symbol(dto.getSymbol())
                .companyName(dto.getCompanyName())
                .industry(dto.getIndustry())
                .isinCode(dto.getIsinCode())
                .nifty500(true)
                .active(true)
                .build();
    }

}