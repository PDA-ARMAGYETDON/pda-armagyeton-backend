package com.example.stock_system.stocks;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.stocks.dto.StockCurrentPrice;
import com.example.stock_system.stocks.dto.StockName;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stocks")
public class StocksController {
    private final StocksService stocksService;

    @GetMapping("/names")
    public ApiResponse<StockName> getStockNames(@RequestParam String stockCode) {
        return new ApiResponse<>(200, true, "종목 이름을 조회했습니다.", stocksService.getStockNameByCode(stockCode));
    }

    @GetMapping("/data")
    public ApiResponse<StockCurrentPrice> getCurrentData(@RequestParam String stockCode){
        return new ApiResponse<>(200, true, "종목 이름을 조회했습니다.", stocksService.getCurrentData(stockCode));
    }
}
