package com.example.group_investment.tradeOffer;

import com.example.common.exception.ErrorCode;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.main-stock-queue.name}")
    private String mainStockQueue;

    public void createTradeOffer(int userId, int teamId, CreateTradeOfferRequest createTradeOfferRequest) {

        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        Rule rule = ruleRepository.findByTeam(team).orElseThrow(
                () -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        if (createTradeOfferRequest.getTradeType() == TradeType.SELL) {
            if (createTradeOfferRequest.getQuantity() > (tradeOfferCommunicator.getNumOfHoldingsFromStockSystem(teamId, createTradeOfferRequest.getCode()) + tradeOfferCommunicator.getNumOfPendingTradeFromStockSystem(teamId, createTradeOfferRequest.getCode()))) {
                throw new TradeOfferException(TradeOfferErrorCode.HOLDINGS_NOT_ENOUGH);
            }
        } else {
            if (createTradeOfferRequest.getQuantity() * createTradeOfferRequest.getWantPrice() > tradeOfferCommunicator.getAvailableAssetFromStockSystem(teamId)) {
                throw new TradeOfferException(TradeOfferErrorCode.ASSET_NOT_ENOUGH);
            }
        }

        TradeOfferDto tradeOfferDto;
        if (createTradeOfferRequest.getTradeType() == TradeType.SELL && tradeOfferCommunicator.getPrdyVrssRtFromStockSystem(createTradeOfferRequest.getCode()) <= ((-1) * rule.getPrdyVrssRt())) {
            tradeOfferDto = tradeOfferConverter.createTradeOfferRequestToUrgentTradeOfferDto(createTradeOfferRequest, member, team);
        } else {
            tradeOfferDto = tradeOfferConverter.createTradeOfferRequestToTradeOfferDto(createTradeOfferRequest, member, team);
        }

        VoteStockToAlarmDto message = new VoteStockToAlarmDto(team.getId(), team.getName());

        MqSender<VoteStockToAlarmDto> mqSender = new MqSender<>(rabbitTemplate);

        mqSender.send(message);

        try {
            tradeOfferRepository.save(tradeOfferDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TRADE_OFFER_SAVE_FAILED);
        }
    }

    public GetAllTradeOffersResponse getAllTradeOffers(int teamId, TradeType type, int page, int size) {

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "offerAt"));

        Page<TradeOffer> tradeOffers = tradeOfferRepository.findAllByTeamAndTradeType(team, type, pageable);

        for (TradeOffer tradeOffer : tradeOffers) {
            if (tradeOffer.getOfferStatus() == OfferStatus.PROGRESS && tradeOffer.isUrgent() && tradeOffer.getOfferAt().isBefore(LocalDateTime.now().plusMinutes(30))) {
                tradeOffer.expireTradeOffer();
                tradeOfferRepository.save(tradeOffer);
            }
        }

        List<TradeOfferResponse> tradeOfferResponses = tradeOffers.stream()
                .map(tradeOffer -> new TradeOfferResponse().builder()
                        .tradeOfferId(tradeOffer.getId())
                        .userName(tradeOffer.getMember().getUser().getName())
                        .stockName(tradeOfferCommunicator.getStockNameFromStockSystem(tradeOffer.getStockCode()).getName())
                        .tradeType(tradeOffer.getTradeType())
                        .offerStatus(tradeOffer.getOfferStatus())
                        .wantPrice(tradeOffer.getWantPrice())
                        .quantity(tradeOffer.getQuantity())
                        .offerAt(tradeOffer.getOfferAt().toLocalDate().toString())
                        .isUrgent(tradeOffer.isUrgent())
                        .upvotes(tradeOffer.getUpvotes())
                        .downvotes(tradeOffer.getDownvotes())
                        .isVote(tradeOfferVoteRepository.existsByTradeOfferAndMember(tradeOffer, tradeOffer.getMember()))
                        .build())
                .toList();

        return new GetAllTradeOffersResponse().builder()
                .tradeOfferResponses(tradeOfferResponses)
                .build();
    }

    public VoteTradeOfferResponse voteTradeOffer(int userId, int teamId, int tradeOfferId, VoteTradeOfferRequest voteTradeOfferRequest) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        Rule rule = ruleRepository.findByTeam(team).orElseThrow(
                () -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        TradeOffer tradeOffer = tradeOfferRepository.findById(tradeOfferId).orElseThrow(
                () -> new TradeOfferException(TradeOfferErrorCode.TRADE_OFFER_NOT_FOUND));

        TradeOfferVoteDto tradeOfferVoteDto = TradeOfferVoteDto.builder()
                .member(member)
                .tradeOffer(tradeOffer)
                .choice(voteTradeOfferRequest.getChoice())
                .build();

        if (tradeOffer.getOfferStatus() != OfferStatus.PROGRESS) {
            throw new TradeOfferException(TradeOfferErrorCode.TRADE_OFFER_EXPIRED);
        }

        if (tradeOfferVoteRepository.existsByTradeOfferAndMember(tradeOffer, member)) {
            throw new TradeOfferException(TradeOfferErrorCode.ALREADY_VOTED);
        }

        tradeOfferVoteRepository.save(tradeOfferVoteDto.toEntity());

        if (tradeOffer.isUrgent()) {
            if (tradeOffer.getOfferAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
                tradeOffer.expireTradeOffer();
                tradeOfferRepository.save(tradeOffer);
                throw new TradeOfferException(TradeOfferErrorCode.TRADE_OFFER_EXPIRED);
            }
        }

        if (voteTradeOfferRequest.getChoice() == Choice.PROS) {
            tradeOffer.vote(true);
        } else {
            tradeOffer.vote(false);
        }

        if (tradeOffer.getUpvotes() >= (tradeOffer.isUrgent() ? rule.getUrgentTradeUpvotes() : rule.getTradeUpvotes())) {
            tradeOffer.approveTradeOffer();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String objToJson = objectMapper.writeValueAsString(tradeOfferConverter.tradeOfferToCreateTradeRequest(tradeOffer));
                rabbitTemplate.convertAndSend(mainStockQueue, objToJson);
            } catch (JsonProcessingException e) {
                throw new TradeOfferException(ErrorCode.JSON_PARSE_ERROR);
            } catch (AmqpException e) {
                throw new TradeOfferException(ErrorCode.MQ_CONNECTION_FAILED);
            }
        } else if (tradeOffer.getDownvotes() > (team.getHeadCount() - (tradeOffer.isUrgent() ? rule.getUrgentTradeUpvotes() : rule.getTradeUpvotes()))) {
            tradeOffer.expireTradeOffer();
        }

        tradeOfferRepository.save(tradeOffer);

        return new VoteTradeOfferResponse().builder()
                .offerStatus(tradeOffer.getOfferStatus())
                .upvotes(tradeOffer.getUpvotes())
                .downvotes(tradeOffer.getDownvotes())
                .build();
    }

    public List<GetPendingTradeOfferResponse> getProgressTradeOffers(int teamId, int page, int size) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        System.out.println("team: " + team.getName());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "offerAt"));

        Page<TradeOffer> tradeOffers = tradeOfferRepository.findAllByTeamAndOfferStatus(team, OfferStatus.PROGRESS, pageable);

        System.out.println("tradeOffers: " + tradeOffers.getSize());

        return tradeOffers.stream()
                .map(tradeOffer -> GetPendingTradeOfferResponse.builder()
                        .stockName(tradeOfferCommunicator.getStockNameFromStockSystem(tradeOffer.getStockCode()).getName())
                        .quantity(tradeOffer.getQuantity())
                        .offerMemberName(tradeOffer.getMember().getUser().getName())
                        .Upvotes(tradeOffer.getUpvotes())
                        .Downvotes(tradeOffer.getDownvotes())
                        .isUrgent(tradeOffer.isUrgent())
                        .build())
                .toList();
    }
}
