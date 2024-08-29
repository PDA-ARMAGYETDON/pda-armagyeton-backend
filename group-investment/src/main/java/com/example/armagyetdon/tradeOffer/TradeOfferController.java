package com.example.armagyetdon.tradeOffer;

import com.example.armagyetdon.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.common.dto.ApiResponse;
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

    @PostMapping()
    public ApiResponse createTradeOffer(@RequestBody CreateTradeOfferRequest createTradeOfferRequest) {
        tradeOfferService.createTradeOffer(createTradeOfferRequest);
        return new ApiResponse(201, true, "매매제안을 생성했습니다.", null);
    }
}
