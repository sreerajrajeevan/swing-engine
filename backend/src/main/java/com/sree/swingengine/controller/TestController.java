package com.sree.swingengine.controller;

import com.sree.swingengine.entity.DailyPrice;
import com.sree.swingengine.market.indicator.calculator.AdxCalculator;
import com.sree.swingengine.market.indicator.model.AdxResult;
import com.sree.swingengine.repository.DailyPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final DailyPriceRepository dailyPriceRepository;
    private final AdxCalculator adxCalculator;

    @GetMapping("/adx")
    public String testAdx() {

        List<DailyPrice> prices = dailyPriceRepository.findByStockIdOrderByTradingDateAsc(423l);

        AdxResult result = adxCalculator.calculate(prices, 14);

        System.out.println("-------------------------------------------");

        for (int i = result.getAdx().size() - 10; i < result.getAdx().size(); i++) {

            System.out.println(
                    prices.get(i).getTradingDate()
                            + " ADX=" + result.getAdx().get(i)
                            + " +DI=" + result.getPlusDi().get(i)
                            + " -DI=" + result.getMinusDi().get(i));
        }

        return "Done";
    }
}