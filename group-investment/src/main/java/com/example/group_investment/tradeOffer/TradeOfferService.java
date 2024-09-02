package com.example.group_investment.tradeOffer;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.member.exception.MemberErrorCode;
import com.example.group_investment.member.exception.MemberException;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import com.example.group_investment.tradeOffer.dto.*;
import com.example.group_investment.tradeOffer.exception.TradeOfferErrorCode;
import com.example.group_investment.tradeOffer.exception.TradeOfferException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeOfferService {
    private final TradeOfferRepository tradeOfferRepository;
    private final MemberRepository memberRepository;
    private final WebClient.Builder webClientBuilder;
    private final TeamRepository teamRepository;

    @Value("${ag.url}")
    private String AG_URL;

    public void createTradeOffer(CreateTradeOfferRequest createTradeOfferRequest) {
        // FIXME: 토큰으로 사용자 아이디와 모임 아이디 가져와야함
        int userId = 1;
        int teamId = 1;

        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        TradeOfferDto tradeOfferDto = TradeOfferDto.builder()
                .member(member)
                .team(team)
                .tradeType(createTradeOfferRequest.getTradeType())
                .recentPrice(createTradeOfferRequest.getRecentPrice())
                .wantPrice(createTradeOfferRequest.getWantPrice())
                .quantity(createTradeOfferRequest.getQuantity())
                .code(createTradeOfferRequest.getCode())
                .build();
        try {
            tradeOfferRepository.save(tradeOfferDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TRADE_OFFER_SAVE_FAILED);
        }
    }

    public GetAllTradeOffersResponse getAllTradeOffers(TradeType type) {
        // FIXME: 토큰으로 사용자 아이디와 모임 아이디 가져와야함
        int userId = 1;
        int teamId = 1;

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        List<TradeOffer> tradeOffers = tradeOfferRepository.findAllByTeamIdAndTradeType(team.getId(), type).orElseThrow(
                () -> new TradeOfferException(TradeOfferErrorCode.TRADE_OFFER_NOT_FOUND)
        );

        List<TradeOfferResponse> tradeOfferResponses = tradeOffers.stream()
                .map(tradeOffer -> new TradeOfferResponse().builder()
                        .userName(tradeOffer.getMember().getUser().getName())
                        .stockName(getStockNameFromStockSystem(tradeOffer.getStockCode()).getName())
                        .tradeType(tradeOffer.getTradeType())
                        .offerStatus(tradeOffer.getOfferStatus())
                        .wantPrice(tradeOffer.getWantPrice())
                        .quantity(tradeOffer.getQuantity())
                        .offerAt(tradeOffer.getOfferAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .userName(tradeOffer.getMember().getUser().getName())
                        .upvotes(tradeOffer.getUpvotes())
                        .downvotes(tradeOffer.getDownvotes())
                        .build())
                .toList();

        return new GetAllTradeOffersResponse().builder()
                .tradeOfferResponses(tradeOfferResponses)
                .build();
    }

    private StockName getStockNameFromStockSystem(String stockCode) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<StockName> stockName = webClient.get()
                .uri(AG_URL + ":8083/api/stocks/names?stockCode=" + stockCode)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<StockName>>() {
                })
                .block();

        if (stockName.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return stockName.getData();
    }
}
