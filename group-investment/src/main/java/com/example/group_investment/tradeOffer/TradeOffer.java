package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TradeOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    private int recentPrice;
    private int wantPrice;
    private int quantity;
    private LocalDateTime offerAt;

    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;

    private int upvotes;
    private int downvotes;


    private String stockCode;

}
