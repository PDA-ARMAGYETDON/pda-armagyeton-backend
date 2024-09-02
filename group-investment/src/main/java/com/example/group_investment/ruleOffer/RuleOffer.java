package com.example.group_investment.ruleOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "RTYPE")
public class RuleOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Rule.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

//    private RuleType rule_type;

    private int upvotes;
    private int downvotes;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;
}
