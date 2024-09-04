package com.example.stock_system.stocks.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStockPricesResponse {
    private List<StockPriceData> stockPrices;
}
