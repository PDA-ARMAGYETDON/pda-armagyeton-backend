package com.example.group_investment.tradeOffer;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.group_investment.tradeOffer.dto.GetAllTradeOffersResponse;
import com.example.group_investment.tradeOffer.dto.VoteTradeOfferRequest;
import com.example.group_investment.tradeOffer.dto.VoteTradeOfferResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trade-offer")
public class TradeOfferController {
    private final TradeOfferService tradeOfferService;

    @Operation(summary = "매매 제안 생성", description = "매매 제안을 생성하는 api입니다.")
    @PostMapping
    public ApiResponse createTradeOffer(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId,
                                        @RequestBody CreateTradeOfferRequest createTradeOfferRequest) {
        System.out.println("userId: " + userId + ", teamId: " + teamId + ", createTradeOfferRequest: " + createTradeOfferRequest.getQuantity());
        tradeOfferService.createTradeOffer(userId, teamId, createTradeOfferRequest);
        return new ApiResponse(201, true, "매매제안을 생성했습니다.", null);
    }

    @Operation(summary = "매매 제안 조회", description = "매매 제안 리스트를 조회하는 api입니다.")
    @GetMapping
    public ApiResponse<GetAllTradeOffersResponse> getAllTradeOffers(@RequestAttribute("teamId") int teamId,
                                                                    @RequestParam TradeType type, @RequestParam int page, @RequestParam int size) {
        return new ApiResponse(200, true, "매매제안을 조회했습니다.", tradeOfferService.getAllTradeOffers(teamId, type, page, size));
    }

    @Operation(summary = "매매 제안 투표", description = "매매 제안에 투표하는 api입니다.")
    @PostMapping("/{id}/vote")
    public ApiResponse<VoteTradeOfferResponse> voteTradeOffer(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId,
                                                              @PathVariable int id, @RequestBody VoteTradeOfferRequest voteTradeOfferRequest) {
        return new ApiResponse<>(201, true, "매매제안에 투표했습니다.", tradeOfferService.voteTradeOffer(userId, teamId, id, voteTradeOfferRequest));
    }
}
