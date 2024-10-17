package com.example.stock_system.backend_only;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.holdings.HoldingsService;
import com.example.stock_system.realTimeStock.RealTimeStockService;
import com.example.stock_system.stocks.StocksService;
import com.example.stock_system.stocks.dto.GetStockPricesResponse;
import com.example.stock_system.stocks.dto.StockName;
import com.example.stock_system.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stock/backend")
public class BackendController {
    private final HoldingsService holdingsService;
    private final StocksService stocksService;
    private final TradeService tradeService;

    private final RealTimeStockService realTimeStockService;

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

    @Operation(summary = "(백엔드 전용)계좌 보유 종목 수 조회", description = "보유 종목 수를 조회하는 API")
    @GetMapping(value = "/holdings/count")
    public ApiResponse<Integer> getNumOfHoldings(@RequestParam int teamId, @RequestParam String code) {
        return new ApiResponse<>(200, true, "보유 종목 수를 조회했습니다.", holdingsService.getNumOfHoldings(teamId, code));
    }


    @Operation(summary = "(백엔드 전용)주문 개수 조회", description = "pending 상태의 주문 개수를 조회하는 API")
    @GetMapping("/trades/count")
    public ApiResponse<Integer> getNumOfTrades(@RequestParam int teamId, @RequestParam String code) {
        return new ApiResponse<>(200, true, "주문 개수를 조회했습니다.", tradeService.getNumOfSellingTrades(teamId, code));
    }

    @Operation(summary = "(백엔드 전용)거래 가능한 예수금 조회", description = "예수금에서 매수 주문 금액을 제외한 여분의 예수금을 조회하는 API")
    @GetMapping("/accounts/asset")
    public ApiResponse<Integer> getAvailableAsset(@RequestParam int teamId) {
        return new ApiResponse<>(200, true, "거래 가능한 예수금을 조회했습니다.", holdingsService.getAvailableAsset(teamId));
    }


    /*
    추후 삭제 예정 jwt에러 때문에 옮겨놓음
     */
    @Operation(summary = "단일 종목 조회",description = "단일 종목의 실시간 시세를 조회")
    @GetMapping(value = "/stocks/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[]> streamByStockCode(@PathVariable String stockCode) {
        realTimeStockService.streamByStockCode(stockCode);
        return realTimeStockService.getStockDataStream(stockCode);
    }

    @GetMapping("/prices")
    public ApiResponse<GetStockPricesResponse> getStockPrices(@RequestParam String code) {
        return new ApiResponse<>(200, true, "종목 가격을 조회했습니다.", stocksService.getStockPrices(code));
    }

    @GetMapping("/current-price")
    public ApiResponse getCurrentPrice(@RequestParam String stockCode){
        return new ApiResponse<>(200,true,"현재가(장 마감후엔 종가) 입니다.",stocksService.getCurrentData(stockCode));
    }


}
