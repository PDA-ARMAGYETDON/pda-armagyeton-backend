package com.example.stock_system.pre_trade;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PreTradeController {
    private final PreTradeService preTradeService;
}
