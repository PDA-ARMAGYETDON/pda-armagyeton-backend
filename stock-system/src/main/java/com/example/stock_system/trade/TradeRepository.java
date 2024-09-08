package com.example.stock_system.trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.stocks.Stocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
    Optional<List<Trade>> findAllByAccountAndStockCodeAndTypeAndStatus(Account account, Stocks stockCode, TradeType type, TradeStatus status);

    List<Trade> findByStatusAndStockCodeAndPriceAndType(TradeStatus status, Stocks stockCode, int price, TradeType type);

    Optional<List<Trade>> findAllByAccountAndTypeAndStatus(Account account, TradeType tradeType, TradeStatus tradeStatus);

    Page<Trade> findAllByAccountAndStatus(Account account, TradeStatus tradeStatus, Pageable pageable);
}
