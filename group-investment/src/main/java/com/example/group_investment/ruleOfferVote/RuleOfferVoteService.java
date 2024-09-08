package com.example.group_investment.ruleOfferVote;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.member.exception.MemberErrorCode;
import com.example.group_investment.member.exception.MemberException;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.rule.RuleRepository;
import com.example.group_investment.rule.dto.RuleDto;
import com.example.group_investment.rule.exception.RuleErrorCode;
import com.example.group_investment.rule.exception.RuleException;
import com.example.group_investment.ruleOffer.*;
import com.example.group_investment.ruleOffer.dto.RuleOfferDto;
import com.example.group_investment.ruleOffer.exception.RuleOfferErrorCode;
import com.example.group_investment.ruleOffer.exception.RuleOfferException;
import com.example.group_investment.ruleOfferVote.dto.CreateRuleOfferVoteRequest;
import com.example.group_investment.ruleOfferVote.dto.RuleOfferVoteDto;
import com.example.group_investment.ruleOfferVote.exception.RuleOfferVoteErrorCode;
import com.example.group_investment.ruleOfferVote.exception.RuleOfferVoteException;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RuleOfferVoteService {

    private final RuleOfferVoteRepository ruleOfferVoteRepository;
    private final MemberRepository memberRepository;
    private final RuleOfferRepository ruleOfferRepository;
    private final RuleRepository ruleRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void create(int ruleOfferId, int teamId, int userId, CreateRuleOfferVoteRequest createRuleOfferVoteRequest) {

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        //1. 멤버를 조회하고
        Member member = memberRepository.findByUserIdAndTeamId(userId,teamId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        // 규칙 제안을 조회하고
        RuleOffer ruleOffer = ruleOfferRepository.findById(ruleOfferId).orElseThrow(() -> new RuleOfferException(RuleOfferErrorCode.RULE_OFFER_NOT_FOUND));
        // 새롭게 투표를 올린다
        RuleOfferVoteDto ruleOfferVoteDto = RuleOfferVoteDto.builder()
                .member(member)
                .ruleOffer(ruleOffer)
                .choice(createRuleOfferVoteRequest.getChoice())
                .build();
        try {
            ruleOfferVoteRepository.save(ruleOfferVoteDto.toEntity());
        } catch (Exception e) {
            throw new RuleOfferVoteException(RuleOfferVoteErrorCode.RULE_OFFER_VOTE_SAVE_FAILED);
        }
        // 2. 조회했던 규칙 제안 테이블 update
        // ruleOfferId로 규칙 제안 테이블을 꺼낸게 ruleOffer
        int updatedUpvotes = ruleOffer.getUpvotes();
        int updatedDownvotes = ruleOffer.getDownvotes();

        if (createRuleOfferVoteRequest.getChoice()== Choice.PROS)
            updatedUpvotes = updatedUpvotes + 1;
        else if (createRuleOfferVoteRequest.getChoice()== Choice.CONS)
            updatedDownvotes = updatedDownvotes + 1;

        int updatedTotalVotes = ruleOffer.getTotalvotes() + 1;
        OfferStatus status = ruleOffer.getStatus();

        //하나라도 반대를 받으면 rejected 로
        if (updatedDownvotes == 1)
            status = OfferStatus.REJECTED;

        if (updatedTotalVotes == team.getHeadCount()) {
            if (updatedTotalVotes == updatedUpvotes)
                status = OfferStatus.APPROVED;
            else
                status = OfferStatus.REJECTED;
        }
        // 규칙 제안 update 하는데 rule 이 필요하니까 여기서 조회
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(()-> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        // update 된 규칙 제안
        RuleOffer updatedRuleOffer = ruleOffer.toBuilder()

                .upvotes(updatedUpvotes)
                .downvotes(updatedDownvotes)
                .totalvotes(updatedTotalVotes)
                .status(status)
                .build();
        ruleOfferRepository.save(updatedRuleOffer);

        // APPROVED 라면 규칙을 업데이트
        if (status == OfferStatus.APPROVED) {

            if (ruleOffer.getRtype()== RuleType.DISBAND) {
                ROfferDisband disband = (ROfferDisband) ruleOffer;
                Rule updatedRule = rule.toBuilder()
                        .maxLossRt(disband.getMaxLossRt())
                        .maxProfitRt(disband.getMaxProfitRt())
                        .build();
                ruleRepository.save(updatedRule);
            }
            else if (ruleOffer.getRtype()== RuleType.PAY_FEE) {
                ROfferPayFee payFee = (ROfferPayFee) ruleOffer;
                Rule updatedRule = rule.toBuilder()
                        .depositAmt(payFee.getDepositAmt())
                        .period(payFee.getPeriod())
                        .payDate(payFee.getPayDate())
                        .build();
                ruleRepository.save(updatedRule);
            }
            else if (ruleOffer.getRtype()== RuleType.URGENT_SALE) {
                ROfferUrgentSale urgentSale= (ROfferUrgentSale) ruleOffer;
                Rule updatedRule = rule.toBuilder()
                        .urgentTradeUpvotes(urgentSale.getTradeUpvotes())
                        .prdyVrssRt(urgentSale.getPrdyVrssRt())
                        .build();
                ruleRepository.save(updatedRule);
            }
            else if (ruleOffer.getRtype()== RuleType.UPVOTE_NUMBER) {
                ROfferUpvotesNumber upvotesNumber = (ROfferUpvotesNumber) ruleOffer;
                Rule updatedRule = rule.toBuilder()
                        .tradeUpvotes(upvotesNumber.getTradeUpvotes())
                        .build();
                ruleRepository.save(updatedRule);
            }
        }
    }
}

