package com.example.stock_system.pre_trade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PreTradeService {
    private final PreTradeRepository preTradeRepository;
}
