package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.stocks.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingsRepository extends JpaRepository<Holdings, Integer> {
    Optional<Holdings> findByAccountAndStockCode(Account account, Stocks stockCode);

    List<Holdings> findByAccount(Account account);
}

