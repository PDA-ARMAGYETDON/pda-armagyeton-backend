package com.example.stock_system.backend_only;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.holdings.HoldingsService;
import com.example.stock_system.stocks.StocksService;
import com.example.stock_system.stocks.dto.StockName;
import com.example.stock_system.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/backend")
public class BackendController {
    private final HoldingsService holdingsService;
    private final StocksService stocksService;
    private final TradeService tradeService;

    @Operation(summary = "(백엔드 전용)종목 이름 조회", description = "종목 코드로 이름을 조회하는 API")
    @GetMapping("/stocks/names")
    public ApiResponse<StockName> getStockNames(@RequestParam String code) {
        return new ApiResponse<>(200, true, "종목 이름을 조회했습니다.", stocksService.getStockNameByCode(code));
    }

    @Operation(summary = "(백엔드 전용)현재 가격 조회", description = "종목 코드로 전일 대비 등략율을 조회하는 API")
    @GetMapping("/stocks/prdyVrssRt")
    public ApiResponse<Double> getPrdyVrssRt(@RequestParam String code) {
        return new ApiResponse<>(200, true, "전일 대비 등락율을 조회했습니다.", stocksService.getCurrentData(code).getChangeRate());
    }

}
