package com.sree.swingengine.repository;

import com.sree.swingengine.entity.DailyIndicator;
import com.sree.swingengine.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyIndicatorRepository extends JpaRepository<DailyIndicator, Long> {

    Optional<DailyIndicator> findByStockAndTradingDate(
            Stock stock,
            LocalDate tradingDate);

    Optional<DailyIndicator> findTopByStockOrderByTradingDateDesc(
            Stock stock);

    List<DailyIndicator> findByStockOrderByTradingDateAsc(
            Stock stock);
}