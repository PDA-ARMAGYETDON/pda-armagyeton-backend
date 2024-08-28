package com.example.stock_system.trade;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TradeController {
    private final TradeService tradeService;
}
