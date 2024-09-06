package com.example.stock_system.ranking;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.ranking.dto.SelectRankingResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "랭킹 조회 API")
    @GetMapping("")
    public ApiResponse<List<SelectRankingResponse>> selectRanking(@RequestAttribute("teamId") int teamId, @RequestParam(value = "seedMoney", required = true) int seedMoney) {
        List<SelectRankingResponse> selectRankingResponseList = rankingService.selectRanking(teamId, seedMoney);
        return new ApiResponse<>(200, true, "랭킹을 조회했습니다.",selectRankingResponseList);

    }

}
