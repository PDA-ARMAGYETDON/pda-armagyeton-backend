package com.example.stock_system.trade;

import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.stocks.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
    List<Trade> findByStatusAndStockCodeAndPriceAndType(TradeStatus status, Stocks stockCode, int price, TradeType type);
}
