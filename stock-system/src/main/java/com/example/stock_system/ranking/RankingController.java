package com.example.stock_system.ranking;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.ranking.dto.SelectRankingWithTeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService rankingService;

    @Operation(summary = "랭킹 조회 API, ex. teamRanking:1 은 Rankings[1] , 해당 구간에 팀이 없다면 -1")
    @GetMapping("/{id}")
    public ApiResponse<SelectRankingWithTeamResponse> selectRanking(@PathVariable int id, @RequestParam(value = "seedMoney", required = true) int seedMoney) {
        SelectRankingWithTeamResponse selectRankingWithTeamResponse = rankingService.selectRanking(id, seedMoney);
        return new ApiResponse<>(200, true, "랭킹을 조회했습니다.",selectRankingWithTeamResponse);

    }
}
