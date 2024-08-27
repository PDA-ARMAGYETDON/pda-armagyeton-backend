package com.example.armagyetdon.ruleOfferVote;

import com.example.armagyetdon.enums.Choice;
import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.ruleOffer.RuleOffer;
import jakarta.persistence.*;

@Entity
public class RuleOfferVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(targetEntity = RuleOffer.class, fetch = FetchType.LAZY)
    @JoinColumn(name="rule_offer_id")
    private RuleOffer ruleOffer;


    @Enumerated(EnumType.STRING)
    private Choice choice;
}
