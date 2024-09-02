package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class TradeOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    private int recentPrice;
    private int wantPrice;
    private int quantity;

    @CreationTimestamp
    private LocalDateTime offerAt;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("PROGRESS")
    private OfferStatus offerStatus = OfferStatus.PROGRESS;

    @ColumnDefault("0")
    private int upvotes;

    @ColumnDefault("0")
    private int downvotes;

    private String stockCode;

    public TradeOffer() {
    }

    @Builder
    public TradeOffer(Member member, TradeType tradeType, int recentPrice, int wantPrice, int quantity, String stockCode) {
        this.member = member;
        this.tradeType = tradeType;
        this.recentPrice = recentPrice;
        this.wantPrice = wantPrice;
        this.quantity = quantity;
        this.stockCode = stockCode;
    }
}
