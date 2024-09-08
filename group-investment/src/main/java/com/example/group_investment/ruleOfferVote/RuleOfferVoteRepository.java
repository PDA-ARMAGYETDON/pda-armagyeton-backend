package com.example.group_investment.ruleOfferVote;

import com.example.group_investment.member.Member;
import com.example.group_investment.ruleOffer.RuleOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleOfferVoteRepository extends JpaRepository<RuleOfferVote, Integer> {
    boolean existsByMemberAndRuleOffer(Member member, RuleOffer ruleOffer);
}
