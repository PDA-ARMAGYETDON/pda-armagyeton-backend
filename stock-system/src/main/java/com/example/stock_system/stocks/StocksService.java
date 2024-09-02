package com.example.stock_system.stocks;

import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StocksService {
    private final StocksRepository stocksRepository;

    public StockName getStockNameByCode(String stockCode) {
        Stocks stocks = stocksRepository.findByCode(stockCode).orElseThrow(
                () -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));
        return new StockName().fromEntity(stocks);
    }
}
