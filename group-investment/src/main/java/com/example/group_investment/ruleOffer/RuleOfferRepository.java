package com.example.group_investment.ruleOffer;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.rule.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleOfferRepository extends JpaRepository<RuleOffer, Integer> {
    List<RuleOffer> findAllByRtypeAndRule(RuleType rType, Rule rule);

}
