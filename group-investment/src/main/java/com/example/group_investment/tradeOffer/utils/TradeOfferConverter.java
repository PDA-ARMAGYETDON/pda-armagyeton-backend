package com.example.group_investment.tradeOffer.utils;

import com.example.group_investment.member.Member;
import com.example.group_investment.team.Team;
import com.example.group_investment.tradeOffer.TradeOffer;
import com.example.group_investment.tradeOffer.dto.CreateTradeOfferRequest;
import com.example.group_investment.tradeOffer.dto.TradeOfferDto;
import com.example.group_investment.tradeOffer.dto.TradeOfferResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class TradeOfferConverter {
    private final TradeOfferCommunicator tradeOfferCommunicator;

    public List<TradeOfferResponse> tradeOfferListToTradeOfferResponseList(Page<TradeOffer> tradeOffers) {
        return tradeOffers.stream()
                .map(tradeOffer -> new TradeOfferResponse().builder()
                        .userName(tradeOffer.getMember().getUser().getName())
                        .stockName(tradeOfferCommunicator.getStockNameFromStockSystem(tradeOffer.getStockCode()).getName())
                        .tradeType(tradeOffer.getTradeType())
                        .offerStatus(tradeOffer.getOfferStatus())
                        .wantPrice(tradeOffer.getWantPrice())
                        .quantity(tradeOffer.getQuantity())
                        .offerAt(tradeOffer.getOfferAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .isUrgent(tradeOffer.isUrgent())
                        .userName(tradeOffer.getMember().getUser().getName())
                        .upvotes(tradeOffer.getUpvotes())
                        .downvotes(tradeOffer.getDownvotes())
                        .build())
                .toList();
    }

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
}
