package com.example.stock_system.trade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
}
