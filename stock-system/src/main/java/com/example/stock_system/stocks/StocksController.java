package com.example.stock_system.stocks;

import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stocks")
public class StocksController {
    private final StocksService stocksService;

    @GetMapping("/names")
    public ApiResponse<StockName> getStockNames(@RequestParam String stockCode) {
        return new ApiResponse<>(200, true, "종목 이름을 조회했습니다.", stocksService.getStockNameByCode(stockCode));
    }
}
