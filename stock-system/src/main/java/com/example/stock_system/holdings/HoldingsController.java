package com.example.stock_system.holdings;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.realTimeStock.RealTimeStockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/holdings")
public class HoldingsController {

    private final HoldingsService holdingsService;
    private final RealTimeStockService realTimeStockService;

    @Operation(summary = "장 마감 후 계좌 보유 종목 data 조회",description = "DB에서 데이터를 불러오는 API")
    @GetMapping(value = "/{teamId}")
    public ApiResponse<List<HoldingsDto>> getHoldings(@PathVariable int teamId) {
        List<HoldingsDto> holdingsList = holdingsService.getHoldingsByTeamId(teamId);
        return new ApiResponse<>(200, true, "보유종목을 조회했습니다.", holdingsList);
    }


}
