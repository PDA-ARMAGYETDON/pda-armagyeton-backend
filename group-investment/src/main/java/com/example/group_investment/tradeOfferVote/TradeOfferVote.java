package com.example.group_investment.tradeOfferVote;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.member.Member;
import com.example.group_investment.tradeOffer.TradeOffer;
import jakarta.persistence.*;

@Entity
public class TradeOfferVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(targetEntity = TradeOffer.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_offer_id")
    private TradeOffer tradeOffer;

    @Enumerated(EnumType.STRING)
    private Choice choice;
}
