package com.example.group_investment.ruleOffer;

import com.example.group_investment.ruleOffer.dto.CreateROfferRequest;
import com.example.group_investment.ruleOffer.dto.CreateROfferResponse;
import com.example.group_investment.ruleOffer.dto.GetROfferResponse;
import com.example.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RuleOfferController {

    private final RuleOfferService ruleOfferService;

    @PostMapping("/api/groups/{id}/rules")
    public ApiResponse<CreateROfferResponse> create(@PathVariable int id, @RequestBody CreateROfferRequest request){
        return new ApiResponse<>(200,
                true,
                "Rule 제안 생성에 성공했습니다.",
                ruleOfferService.create(id, request));
    }

    @GetMapping("/api/groups/{id}/rules/offers")
    public ApiResponse<GetROfferResponse> get(@PathVariable int id){
        System.out.println("이거 실행됨ㅋㅋ");

        return new ApiResponse(200,
                true,
                "Rule 제안 조회에 성공했습니다.",
                ruleOfferService.get(id));
    }
}
