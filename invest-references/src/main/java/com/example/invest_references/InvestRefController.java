package com.example.invest_references;

import com.example.common.dto.ApiResponse;
import com.example.invest_references.dto.MarketIndexResponse;
import com.example.invest_references.dto.News;
import com.example.invest_references.dto.ShinhanData;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class InvestRefController {
    private final InvestRefService investRefService;

    @Operation(summary = "주요 뉴스 조회", description = "네이버 증권에서 주요 뉴스 5개를 가져옵니다.")
    @GetMapping("/api/news")
    public ApiResponse<List<News>> getNews() {
        return new ApiResponse<>(200, true, "주요 뉴스를 가져왔습니다.", investRefService.getNews());
    }

    @Operation(summary = "코스피/코스닥 지수 조회", description = "네이버 증권에서 코스피/코스닥 지수를 가져옵니다.")
    @GetMapping("/api/market-index")
    public ApiResponse<MarketIndexResponse> getMarketIndex() {
        return new ApiResponse<>(200, true, "코스피/코스닥 지수를 가져왔습니다.", investRefService.getMarketIndex());
    }

    @Operation(summary = "증권_공개형API_인기주식_지금뜨는테마보기",description = "신투 기준 인기 주식 가져옴")
    @GetMapping("/api/rising-ranking")
    public ApiResponse<List<ShinhanData>> getRisingRanking() throws JsonProcessingException{
        return new ApiResponse<>(200, true, "실시간 현재가 조회수가 높은 종목 출력",investRefService.getRisingRanking());
    }

    @Operation(summary = "증권_공개형API_핫이슈종목_시장상위종목-거래량 기준",description = "신투 기준 거래량 상위 주식 가져옴")
    @GetMapping("/api/issue/volume")
    public ApiResponse<List<ShinhanData>> getVolumeRanking() throws JsonProcessingException{
        return new ApiResponse<>(200,true,"거래량 기준 높은 종목 출력",investRefService.getVolumeRanking());
    }


    @Operation(summary = "증권_공개형API_핫이슈종목_시장상위종목-수익률 기준",description = "신투 기준 수익률 상위 주식 가져옴")
    @GetMapping("/api/issue/earning")
    public ApiResponse<List<ShinhanData>> getEarningRanking() throws JsonProcessingException{
        return new ApiResponse<>(200,true,"수익률 기준 높은 종목 출력",investRefService.getEarningRanking());
    }
}
