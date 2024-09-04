package com.example.group_investment.member;

import com.example.group_investment.enums.MemberRole;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.example.group_investment.ruleOfferVote.RuleOfferVote;
import com.example.group_investment.team.Team;
import com.example.group_investment.tradeOffer.TradeOffer;
import com.example.group_investment.tradeOffer.TradeOfferVote;
import com.example.group_investment.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member")
    private List<TradeOffer> tradeOffers;

    @OneToMany(mappedBy = "member")
    private List<TradeOfferVote> tradeOfferVotes;

    @OneToMany(mappedBy = "member")
    private List<RuleOffer> ruleOffers;

    @OneToMany(mappedBy = "member")
    private List<RuleOfferVote> ruleOfferVotes;

    @Builder
    public Member(Team team, User user, MemberRole role, LocalDateTime createdAt) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Member() {

    }
}
