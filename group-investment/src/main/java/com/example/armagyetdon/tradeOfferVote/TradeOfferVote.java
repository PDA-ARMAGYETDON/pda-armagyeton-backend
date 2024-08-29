package com.example.armagyetdon.tradeOfferVote;

import com.example.armagyetdon.enums.Choice;
import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.tradeOffer.TradeOffer;
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
