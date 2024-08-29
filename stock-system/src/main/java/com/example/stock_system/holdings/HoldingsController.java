package com.example.stock_system.holdings;

import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HoldingsController {
    private final HoldingsService holdingsService;

    @GetMapping("/{id}")
    public ApiResponse<Holdings> getHoldings(@PathVariable int id) {
        return new ApiResponse<>(200, true, "보유종목을 조회했습니다.", null);
    }
}
