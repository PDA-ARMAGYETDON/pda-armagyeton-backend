package com.example.stock_system.stocks;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StocksService {
    private final StocksRepository stocksRepository;

    public StockName getStockNameByCode(String stockCode) {
        return new StockName().fromEntity(stocksRepository.findByCode(stockCode));
    }
}
