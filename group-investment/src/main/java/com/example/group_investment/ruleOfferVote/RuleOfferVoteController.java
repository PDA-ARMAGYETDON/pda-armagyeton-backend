package com.example.group_investment.ruleOfferVote;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.ruleOfferVote.dto.CreateRuleOfferVoteRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RuleOfferVoteController {

    private final RuleOfferVoteService ruleOfferVoteService;

    @Operation(summary = "규칙 제안 투표 올리는 API ")
    @PostMapping(("/api/rules/{id}/vote"))
    public ApiResponse create(@PathVariable("id") int id, @RequestAttribute("teamId") int teamId, @RequestAttribute("userId") int userId, @RequestBody CreateRuleOfferVoteRequest createRuleOfferVoteRequest) {
        ruleOfferVoteService.create(id, teamId, userId, createRuleOfferVoteRequest);
        return new ApiResponse<>(200, true, "투표를 했습니다.", null);
    }

}

