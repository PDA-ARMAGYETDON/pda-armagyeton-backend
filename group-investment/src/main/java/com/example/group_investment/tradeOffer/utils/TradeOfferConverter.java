package com.example.group_investment.tradeOffer.utils;

import com.example.group_investment.member.Member;
import com.example.group_investment.team.Team;
import com.example.group_investment.tradeOffer.TradeOffer;
import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.group_investment.tradeOffer.dto.CreateTradeRequest;
import com.example.group_investment.tradeOffer.dto.TradeOfferDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TradeOfferConverter {
    private final TradeOfferCommunicator tradeOfferCommunicator;

    public TradeOfferDto createTradeOfferRequestToUrgentTradeOfferDto(CreateTradeOfferRequest createTradeOfferRequest, Member member, Team team) {
        return new TradeOfferDto().builder()
                .member(member)
                .team(team)
                .isUrgent(true)
                .tradeType(createTradeOfferRequest.getTradeType())
                .recentPrice(createTradeOfferRequest.getRecentPrice())
                .wantPrice(createTradeOfferRequest.getWantPrice())
                .quantity(createTradeOfferRequest.getQuantity())
                .code(createTradeOfferRequest.getCode())
                .build();
    }

    public TradeOfferDto createTradeOfferRequestToTradeOfferDto(CreateTradeOfferRequest createTradeOfferRequest, Member member, Team team) {
        return new TradeOfferDto().builder()
                .member(member)
                .team(team)
                .isUrgent(false)
                .tradeType(createTradeOfferRequest.getTradeType())
                .recentPrice(createTradeOfferRequest.getRecentPrice())
                .wantPrice(createTradeOfferRequest.getWantPrice())
                .quantity(createTradeOfferRequest.getQuantity())
                .code(createTradeOfferRequest.getCode())
                .build();
    }

    public CreateTradeRequest tradeOfferToCreateTradeRequest(TradeOffer tradeOffer) {
        return new CreateTradeRequest().builder()
                .teamId(tradeOffer.getTeam().getId())
                .stockCode(tradeOffer.getStockCode())
                .tradeType(tradeOffer.getTradeType())
                .price(tradeOffer.getWantPrice())
                .quantity(tradeOffer.getQuantity())
                .build();
    }
}
