package com.example.group_investment.ruleOfferVote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleOfferVoteRepository extends JpaRepository<RuleOfferVote, Integer> {

}
