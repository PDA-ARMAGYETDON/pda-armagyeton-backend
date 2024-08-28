package com.example.stock_system.holdings;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HoldingsService {
    private final HoldingsRepository holdingsRepository;
}
