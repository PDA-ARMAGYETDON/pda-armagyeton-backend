package com.example.stock_system.stocks;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class StocksController {
    private final StocksService stocksService;
}
