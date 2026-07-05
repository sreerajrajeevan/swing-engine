package com.sree.swingengine.repository;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPriceRepository extends JpaRepository<DailyPrice, Long> {

    Optional<DailyPrice> findByStockAndTradeDate(Stock stock, LocalDate tradeDate);

}