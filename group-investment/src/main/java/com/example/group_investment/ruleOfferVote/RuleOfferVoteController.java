package com.example.group_investment.ruleOfferVote;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.ruleOfferVote.dto.CreateRuleOfferVoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RuleOfferVoteController {

    private final RuleOfferVoteService ruleOfferVoteService;

    @PostMapping(("/api/rules/{id}/vote"))
    public ApiResponse create(@PathVariable("id") int id, @RequestBody CreateRuleOfferVoteRequest createRuleOfferVoteRequest) {
        ruleOfferVoteService.create(id, createRuleOfferVoteRequest);
        return new ApiResponse<>(200, true, "투표를 했습니다.", null);
    }

}

