package com.example.stock_system.trade;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.trade.dto.GetTradeResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;

    @Operation(summary = "거래내역 조회", description = "거래내역을 조회하는 API")
    @GetMapping
    public ApiResponse<List<GetTradeResponse>> getTrades(@RequestAttribute("teamId") int teamId,
                                                         @RequestParam int page, @RequestParam int size) {
        return new ApiResponse<>(200, true, "거래내역을 조회했습니다.", tradeService.getTrades(teamId, page, size));
    }
}
