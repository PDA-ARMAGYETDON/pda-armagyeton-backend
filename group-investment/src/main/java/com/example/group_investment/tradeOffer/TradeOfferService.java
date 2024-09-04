package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.member.exception.MemberErrorCode;
import com.example.group_investment.member.exception.MemberException;
import com.example.group_investment.rabbitMq.MqSender;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.rule.RuleRepository;
import com.example.group_investment.rule.exception.RuleErrorCode;
import com.example.group_investment.rule.exception.RuleException;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import com.example.group_investment.tradeOffer.dto.*;
import com.example.group_investment.tradeOffer.exception.TradeOfferErrorCode;
import com.example.group_investment.tradeOffer.exception.TradeOfferException;
import com.example.group_investment.tradeOffer.utils.TradeOfferCommunicator;
import com.example.group_investment.tradeOffer.utils.TradeOfferConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeOfferService {
    private final TradeOfferRepository tradeOfferRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final RuleRepository ruleRepository;
    private final TradeOfferVoteRepository tradeOfferVoteRepository;
    private final TradeOfferConverter tradeOfferConverter;
    private final TradeOfferCommunicator tradeOfferCommunicator;

    private final MqSender mqSender;

    public void createTradeOffer(CreateTradeOfferRequest createTradeOfferRequest) {
        // FIXME: 토큰으로 사용자 아이디와 모임 아이디 가져와야함
        int userId = 1;
        int teamId = 1;

        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        Rule rule = ruleRepository.findByTeam(team).orElseThrow(
                () -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        TradeOfferDto tradeOfferDto;
        if (createTradeOfferRequest.getTradeType() == TradeType.SELL && tradeOfferCommunicator.getPrdyVrssRtFromStockSystem(createTradeOfferRequest.getCode()) <= ((-1) * rule.getPrdyVrssRt())) {
            tradeOfferDto = tradeOfferConverter.createTradeOfferRequestToUrgentTradeOfferDto(createTradeOfferRequest, member, team);
        } else {
            tradeOfferDto = tradeOfferConverter.createTradeOfferRequestToTradeOfferDto(createTradeOfferRequest, member, team);
        }

        try {
            tradeOfferRepository.save(tradeOfferDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TRADE_OFFER_SAVE_FAILED);
        }
    }

    public GetAllTradeOffersResponse getAllTradeOffers(TradeType type, int page, int size) {
        // FIXME: 토큰으로 사용자 아이디와 모임 아이디 가져와야함
        int userId = 1;
        int teamId = 1;

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "offerAt"));

        Page<TradeOffer> tradeOffers = tradeOfferRepository.findAllByTeamIdAndTradeType(team.getId(), type, pageable);

        for (TradeOffer tradeOffer : tradeOffers) {
            if (tradeOffer.getOfferStatus() == OfferStatus.PROGRESS && tradeOffer.isUrgent() && tradeOffer.getOfferAt().isBefore(LocalDateTime.now().plusMinutes(30))) {
                tradeOffer.expireTradeOffer();
                tradeOfferRepository.save(tradeOffer);
            }
        }

        List<TradeOfferResponse> tradeOfferResponses = tradeOfferConverter.tradeOfferListToTradeOfferResponseList(tradeOffers);

        return new GetAllTradeOffersResponse().builder()
                .tradeOfferResponses(tradeOfferResponses)
                .build();
    }

    public VoteTradeOfferResponse voteTradeOffer(int userId, int teamId, int tradeOfferId, VoteTradeOfferRequest voteTradeOfferRequest) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Rule rule = ruleRepository.findByTeam(teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND))).orElseThrow(
                () -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        TradeOffer tradeOffer = tradeOfferRepository.findById(tradeOfferId).orElseThrow(
                () -> new TradeOfferException(TradeOfferErrorCode.TRADE_OFFER_NOT_FOUND));

        TradeOfferVoteDto tradeOfferVoteDto = TradeOfferVoteDto.builder()
                .member(member)
                .tradeOffer(tradeOffer)
                .choice(voteTradeOfferRequest.getChoice())
                .build();

        tradeOfferVoteRepository.save(tradeOfferVoteDto.toEntity());

        if (voteTradeOfferRequest.getChoice() == Choice.PROS) {
            tradeOffer.vote(true);
        } else tradeOffer.vote(false);

        if (tradeOffer.isUrgent() && (tradeOffer.getUpvotes() + tradeOffer.getDownvotes()) >= rule.getUrgentTradeUpvotes()) {
            if (tradeOffer.getUpvotes() >= rule.getUrgentTradeUpvotes()) {
                tradeOffer.approveTradeOffer();

            } else {
                tradeOffer.expireTradeOffer();
            }
            tradeOfferRepository.save(tradeOffer);
            CreateTradeRequest createTradeRequest = new CreateTradeRequest().builder()
                    .tradeType(tradeOffer.getTradeType())
                    .stockCode(tradeOffer.getStockCode())
                    .quantity(tradeOffer.getQuantity())
                    .price(tradeOffer.getWantPrice())
                    .build();
            mqSender.send(createTradeRequest);
        } else if (!tradeOffer.isUrgent() && (tradeOffer.getUpvotes() + tradeOffer.getDownvotes()) >= rule.getTradeUpvotes()) {
            if (tradeOffer.getUpvotes() >= rule.getTradeUpvotes()) {
                tradeOffer.approveTradeOffer();
            } else {
                tradeOffer.expireTradeOffer();
            }
            tradeOfferRepository.save(tradeOffer);
            CreateTradeRequest createTradeRequest = new CreateTradeRequest().builder()
                    .tradeType(tradeOffer.getTradeType())
                    .stockCode(tradeOffer.getStockCode())
                    .quantity(tradeOffer.getQuantity())
                    .price(tradeOffer.getWantPrice())
                    .build();
            mqSender.send(createTradeRequest);
        }

        return new VoteTradeOfferResponse().builder()
                .offerStatus(tradeOffer.getOfferStatus())
                .upvotes(tradeOffer.getUpvotes())
                .downvotes(tradeOffer.getDownvotes())
                .build();
    }
}
