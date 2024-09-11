package com.example.group_investment.ruleOffer;

import com.example.common.exception.ErrorCode;
import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.member.exception.MemberErrorCode;
import com.example.group_investment.member.exception.MemberException;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.rule.RuleRepository;
import com.example.group_investment.rule.exception.RuleErrorCode;
import com.example.group_investment.rule.exception.RuleException;
import com.example.group_investment.ruleOffer.dto.*;
import com.example.group_investment.ruleOfferVote.RuleOfferVoteRepository;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleOfferService {

    private final RuleOfferRepository ruleOfferRepository;
    private final RuleRepository ruleRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final RuleOfferVoteRepository ruleOfferVoteRepository;
    private final RabbitTemplate rabbitTemplate;

    public CreateROfferResponse create(int jwtUserId, int jwtTeamId, int teamId, CreateROfferRequest request) {
        if (jwtTeamId != teamId) {
            throw new RuleException(RuleErrorCode.FORBIDDEN_ERROR);
        }
        // 규칙 제안 생성하기
        // 규칙 제안을 각 타입별로 type에 따라 Join된 테이블에 저장
        RuleType type = request.getType();

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        // Team에 속한 멤버 수
        int totalMember = team.getSizeOfMembers();

        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        Member member = memberRepository.findByUserIdAndTeamId(jwtUserId, teamId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        RuleOffer ruleOffer = request.toEntity(rule, member, totalMember);
        ruleOfferRepository.save(ruleOffer);

        //Mq 전송
//        MqSender<VoteRuleToAlarmDto> mqSender = new MqSender<>(rabbitTemplate);
//        mqSender.send(new VoteRuleToAlarmDto(teamId, team.getName()));

        VoteRuleToAlarmDto data = new VoteRuleToAlarmDto(teamId, team.getName());
        try {
            log.info("규칙 제안 알림 전송 from TradeOfferService");

            ObjectMapper objectMapper = new ObjectMapper();
            String objToJson = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend("rule_to_alarm", objToJson);

            return CreateROfferResponse.builder()
                    .type(type).build();

        } catch (JsonProcessingException e) {
            throw new RuleException(ErrorCode.JACKSON_PROCESS_ERROR);
        }


    }

    public GetROfferResponse get(int userId, int jwtTeamId, int teamId) {
        if (jwtTeamId != teamId) {
            throw new RuleException(RuleErrorCode.FORBIDDEN_ERROR);
        }

        // 규칙 제안 조회하기
        // 규칙 제안을 각 타입별로 type에 따라 Join된 테이블에서 조회

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        // 멤버 id 찾기
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 규칙 제안 조회
        // 규칙타입 Disband
        List<GetROfferResponseDisband> offersDisband = getGetROfferResponseDisbands(rule, member);

        // 규칙타입 PayFee
        List<GetROfferResponsePayFee> offersPayFee = getGetROfferResponsePayFees(rule, member);

        // 규칙타입 UpvoteNumber
        List<GetROfferResponseUpvoteNumber> offersUpvoteNumber = getGetROfferResponseUpvoteNumbers(rule, member);

        // 규칙타입 UrgentSale
        List<GetROfferResponseUrgentSale> offersUrgentSale = getGetROfferResponseUrgentSales(rule, member);

        return GetROfferResponse.builder()
                .disbandOffers(offersDisband)
                .payFeeOffers(offersPayFee)
                .upvoteNumberOffers(offersUpvoteNumber)
                .urgentSaleOffers(offersUrgentSale)
                .build();
    }

    private List<GetROfferResponseDisband> getGetROfferResponseDisbands(Rule rule, Member member) {
        List<RuleOffer> offers = ruleOfferRepository.findAllByRtypeAndRule(RuleType.DISBAND, rule);

        return offers.stream()
                .filter(ruleOffer -> ruleOffer instanceof ROfferDisband)
                .map(ruleOffer -> (ROfferDisband) ruleOffer)
                .map(offerDisband -> GetROfferResponseDisband.builder()
                        .type(RuleType.DISBAND)
                        .id(offerDisband.getId())
                        .status(offerDisband.getStatus())
                        .isVote(checkIfVoteExists(member, offerDisband))
                        .upvotes(offerDisband.getUpvotes())
                        .downvotes(offerDisband.getDownvotes())
                        .totalvotes(offerDisband.getTotalvotes())
                        .maxLossRt(offerDisband.getMaxLossRt())
                        .maxProfitRt(offerDisband.getMaxProfitRt())
                        .name(member.getUser().getName())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private List<GetROfferResponsePayFee> getGetROfferResponsePayFees(Rule rule, Member member) {
        List<RuleOffer> offers = ruleOfferRepository.findAllByRtypeAndRule(RuleType.PAY_FEE, rule);
        List<GetROfferResponsePayFee> offersPayFee = offers.stream()
                .filter(ruleOffer -> ruleOffer instanceof ROfferPayFee)
                .map(ruleOffer -> (ROfferPayFee) ruleOffer)
                .map(offerPayFee -> GetROfferResponsePayFee.builder()
                        .type(RuleType.PAY_FEE)
                        .id(offerPayFee.getId())
                        .status(offerPayFee.getStatus())
                        .isVote(checkIfVoteExists(member, offerPayFee))
                        .upvotes(offerPayFee.getUpvotes())
                        .downvotes(offerPayFee.getDownvotes())
                        .totalvotes(offerPayFee.getTotalvotes())
                        .depositAmt(offerPayFee.getDepositAmt())
                        .period(offerPayFee.getPeriod())
                        .payDate(offerPayFee.getPayDate())
                        .name(member.getUser().getName())
                        .build()
                )
                .collect(Collectors.toList());
        return offersPayFee;
    }

    private List<GetROfferResponseUpvoteNumber> getGetROfferResponseUpvoteNumbers(Rule rule, Member member) {
        List<RuleOffer> offers = ruleOfferRepository.findAllByRtypeAndRule(RuleType.UPVOTE_NUMBER, rule);
        List<GetROfferResponseUpvoteNumber> offersUpvoteNumber = offers.stream()
                .filter(ruleOffer -> ruleOffer instanceof ROfferUpvotesNumber)
                .map(ruleOffer -> (ROfferUpvotesNumber) ruleOffer)
                .map(offerUpvoteNumber -> GetROfferResponseUpvoteNumber.builder()
                        .type(RuleType.UPVOTE_NUMBER)
                        .id(offerUpvoteNumber.getId())
                        .status(offerUpvoteNumber.getStatus())
                        .isVote(checkIfVoteExists(member, offerUpvoteNumber))
                        .upvotes(offerUpvoteNumber.getUpvotes())
                        .downvotes(offerUpvoteNumber.getDownvotes())
                        .totalvotes(offerUpvoteNumber.getTotalvotes())
                        .tradeUpvotes(offerUpvoteNumber.getTradeUpvotes())
                        .name(member.getUser().getName())
                        .build()
                )
                .collect(Collectors.toList());
        return offersUpvoteNumber;
    }

    private List<GetROfferResponseUrgentSale> getGetROfferResponseUrgentSales(Rule rule, Member member) {
        List<RuleOffer> offers = ruleOfferRepository.findAllByRtypeAndRule(RuleType.URGENT_SALE, rule);
        List<GetROfferResponseUrgentSale> offersUrgentSale = offers.stream()
                .filter(ruleOffer -> ruleOffer instanceof ROfferUrgentSale)
                .map(ruleOffer -> (ROfferUrgentSale) ruleOffer)
                .map(offerUrgentSale -> GetROfferResponseUrgentSale.builder()
                        .type(RuleType.URGENT_SALE)
                        .id(offerUrgentSale.getId())
                        .status(offerUrgentSale.getStatus())
                        .isVote(checkIfVoteExists(member, offerUrgentSale))
                        .upvotes(offerUrgentSale.getUpvotes())
                        .downvotes(offerUrgentSale.getDownvotes())
                        .totalvotes(offerUrgentSale.getTotalvotes())
                        .tradeUpvotes(offerUrgentSale.getTradeUpvotes())
                        .prdyVrssRt(offerUrgentSale.getPrdyVrssRt())
                        .name(member.getUser().getName())
                        .build()
                )
                .collect(Collectors.toList());
        return offersUrgentSale;
    }

    public boolean checkIfVoteExists(Member member, RuleOffer ruleOffer) {
        return ruleOfferVoteRepository.existsByMemberAndRuleOffer(member, ruleOffer);
        // 존재하면 true, 존재하지 않으면 false
    }
}
