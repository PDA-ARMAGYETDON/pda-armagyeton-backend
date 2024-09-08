package com.example.group_investment.ruleOffer;

import com.example.group_investment.ruleOffer.dto.CreateROfferRequest;
import com.example.group_investment.ruleOffer.dto.CreateROfferResponse;
import com.example.group_investment.ruleOffer.dto.GetROfferResponse;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RuleOfferController {

    private final RuleOfferService ruleOfferService;

    @Operation(
            summary = "규칙 제안",
            description = "Request Body는 Rule Type에 따라서 다른 값을 보냅니다. 모르겠으면 Schema 또는 노션 api 문서를 보세요. or 경서에게 문의"
    )
    @PostMapping("/api/groups/{id}/rules")
    public ApiResponse<CreateROfferResponse> create(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId,
                                                    @PathVariable int id, @RequestBody CreateROfferRequest request){
        return new ApiResponse<>(200,
                true,
                "Rule 제안 생성에 성공했습니다.",
                ruleOfferService.create(userId, teamId, id, request));
    }

    @Operation(
            summary = "규칙 제안들을 조회",
            description = "response는 규칙의 타입 별로 리스트를 뽑아 줍니다."
    )
    @GetMapping("/api/groups/{id}/rules/offers")
    public ApiResponse<GetROfferResponse> get(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId, @PathVariable int id){
        return new ApiResponse(200,
                true,
                "Rule 제안 조회에 성공했습니다.",
                ruleOfferService.get(userId, teamId, id));
    }
}
