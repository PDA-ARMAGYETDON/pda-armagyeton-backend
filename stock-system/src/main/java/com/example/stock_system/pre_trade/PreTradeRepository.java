package com.example.stock_system.pre_trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreTradeRepository extends JpaRepository<PreTrade, Integer> {
}
