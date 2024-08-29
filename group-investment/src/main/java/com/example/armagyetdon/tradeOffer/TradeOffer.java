package com.example.armagyetdon.tradeOffer;

import com.example.armagyetdon.enums.OfferStatus;
import com.example.armagyetdon.enums.TradeType;
import com.example.armagyetdon.member.Member;
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
