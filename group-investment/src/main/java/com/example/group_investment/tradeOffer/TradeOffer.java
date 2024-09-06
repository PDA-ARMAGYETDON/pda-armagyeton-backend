package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import com.example.group_investment.team.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
public class TradeOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    private int recentPrice;
    private int wantPrice;
    private int quantity;

    @CreationTimestamp
    private LocalDateTime offerAt;

    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus = OfferStatus.PROGRESS;

    @ColumnDefault("0")
    private int upvotes;

    @ColumnDefault("0")
    private int downvotes;

    private boolean isUrgent;

    private String stockCode;

    public TradeOffer() {
    }

    @Builder
    public TradeOffer(Member member, Team team, boolean isUrgent, TradeType tradeType, int recentPrice, int wantPrice, int quantity, String stockCode) {
        this.member = member;
        this.team = team;
        this.isUrgent = isUrgent;
        this.tradeType = tradeType;
        this.recentPrice = recentPrice;
        this.wantPrice = wantPrice;
        this.quantity = quantity;
        this.stockCode = stockCode;
    }

    public void approveTradeOffer() {
        this.offerStatus = OfferStatus.APPROVED;
    }

    public void expireTradeOffer() {
        this.offerStatus = OfferStatus.REJECTED;
    }

    public void vote(boolean isUpvote) {
        if (isUpvote) {
            this.upvotes++;
        } else {
            this.downvotes++;
        }
    }
}
