package com.example.group_investment.tradeOffer;

import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
