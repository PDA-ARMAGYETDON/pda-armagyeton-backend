package com.example.stock_system.stocks;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.stocks.dto.GetStockPricesResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "헬스체크 API", description = "서버가 정상적으로 동작하는지 확인하는 API")
    @GetMapping("/health-check")
    public ApiResponse healthCheck() {
        return new ApiResponse<>(200, true, "서버가 정상적으로 동작합니다.", null);
    }

    @GetMapping("/prices")
    public ApiResponse<GetStockPricesResponse> getStockPrices(@RequestParam String code) {
        return new ApiResponse<>(200, true, "종목 가격을 조회했습니다.", stocksService.getStockPrices(code));
    }
}
