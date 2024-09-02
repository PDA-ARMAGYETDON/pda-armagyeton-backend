package com.example.group_investment.tradeOffer;

import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.common.dto.ApiResponse;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.group_investment.tradeOffer.dto.GetAllTradeOffersResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trade-offer")
public class TradeOfferController {
    private final TradeOfferService tradeOfferService;

    @Operation(summary = "매매 제안 생성", description = "매매 제안을 생성하는 api입니다.")
    @PostMapping()
    public ApiResponse createTradeOffer(@RequestBody CreateTradeOfferRequest createTradeOfferRequest) {
        tradeOfferService.createTradeOffer(createTradeOfferRequest);
        return new ApiResponse(201, true, "매매제안을 생성했습니다.", null);
    }

    @Operation(summary = "매매 제안 조회", description = "매매 제안 리스트를 조회하는 api입니다.")
    @GetMapping()
    public ApiResponse<GetAllTradeOffersResponse> getAllTradeOffers(@RequestParam TradeType type) {
        return new ApiResponse(200, true, "매매제안을 조회했습니다.", tradeOfferService.getAllTradeOffers(type));
    }
}
