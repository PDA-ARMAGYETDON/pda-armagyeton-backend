package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.member.Member;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class TradeOfferVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(targetEntity = TradeOffer.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_offer_id")
    private TradeOffer tradeOffer;

    @Enumerated(EnumType.STRING)
    private Choice choice;

}
