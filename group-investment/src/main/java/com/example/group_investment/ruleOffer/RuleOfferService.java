package com.example.group_investment.ruleOffer;

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
import com.example.group_investment.ruleOfferVote.RuleOfferVote;
import com.example.group_investment.ruleOfferVote.RuleOfferVoteRepository;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RuleOfferService {

    private final RuleOfferRepository ruleOfferRepository;
    private final RuleRepository ruleRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final RuleOfferVoteRepository ruleOfferVoteRepository;

    public CreateROfferResponse create(int teamId, CreateROfferRequest request) {
        // 규칙 제안 생성하기
        // 규칙 제안을 각 타입별로 type에 따라 Join된 테이블에 저장

        // 규칙 타입
        RuleType type = request.getType();

        // Team 찾기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        // Team에 속한 멤버 수
        int totalMember = team.getSizeOfMembers();

        // 규칙
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        // 멤버 찾기
        Member member = memberRepository.findById(1).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)); //memberId); // FIXME : memberId 유저 정보로 변경

        // request를 entity로 변환
        RuleOffer ruleOffer = request.toEntity(rule, member, totalMember);

        // entity를 저장
        ruleOfferRepository.save(ruleOffer);

        return CreateROfferResponse.builder()
                .type(type).build();
    }

    public GetROfferResponse get(int teamId, int userId) {
        // 규칙 제안 조회하기
        // 규칙 제안을 각 타입별로 type에 따라 Join된 테이블에서 조회

        // Team 찾기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));

        // 멤버 id 찾기
        Member member = memberRepository.findByUserIdAndTeamId(userId,teamId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // repository에서 규칙 제안 조회
        // 규칙타입 Disband GetROfferResponseType
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
                        .isVote(checkIfVoteExists(member, offerDisband))
                        .upvotes(offerDisband.getUpvotes())
                        .downvotes(offerDisband.getDownvotes())
                        .totalvotes(offerDisband.getTotalvotes())
                        .maxLossRt(offerDisband.getMaxLossRt())
                        .maxProfitRt(offerDisband.getMaxProfitRt())
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
                        .isVote(checkIfVoteExists(member, offerPayFee))
                        .upvotes(offerPayFee.getUpvotes())
                        .downvotes(offerPayFee.getDownvotes())
                        .totalvotes(offerPayFee.getTotalvotes())
                        .depositAmt(offerPayFee.getDepositAmt())
                        .period(offerPayFee.getPeriod())
                        .payDate(offerPayFee.getPayDate())
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
                        .isVote(checkIfVoteExists(member, offerUpvoteNumber))
                        .upvotes(offerUpvoteNumber.getUpvotes())
                        .downvotes(offerUpvoteNumber.getDownvotes())
                        .totalvotes(offerUpvoteNumber.getTotalvotes())
                        .tradeUpvotes(offerUpvoteNumber.getTradeUpvotes())
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
                        .isVote(checkIfVoteExists(member, offerUrgentSale))
                        .upvotes(offerUrgentSale.getUpvotes())
                        .downvotes(offerUrgentSale.getDownvotes())
                        .totalvotes(offerUrgentSale.getTotalvotes())
                        .tradeUpvotes(offerUrgentSale.getTradeUpvotes())
                        .prdyVrssRt(offerUrgentSale.getPrdyVrssRt())
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
