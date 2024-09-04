package com.example.stock_system.trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.stocks.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
    List<Trade> findByStatusAndStockCodeAndPrice(TradeStatus status, Stocks stockCode, int price);

    Optional<List<Trade>> findAllByAccountAndStocksAndTypeAndStatus(Account account, Stocks stocks, TradeType type, TradeStatus status);
}
