package com.example.group_investment.member;

import com.example.group_investment.enums.JoinStatus;
import com.example.group_investment.enums.MemberRole;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.example.group_investment.ruleOfferVote.RuleOfferVote;
import com.example.group_investment.team.Team;
import com.example.group_investment.tradeOffer.TradeOffer;
import com.example.group_investment.tradeOfferVote.TradeOfferVote;
import com.example.group_investment.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.example.group_investment.enums.JoinStatus.DROP;

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

    @JoinColumn(name = "join_status")
    private JoinStatus joinStatus;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime dropedAt;

    @OneToMany(mappedBy = "member")
    private List<TradeOffer> tradeOffers;

    @OneToMany(mappedBy = "member")
    private List<TradeOfferVote> tradeOfferVotes;

    @OneToMany(mappedBy = "member")
    private List<RuleOffer> ruleOffers;

    @OneToMany(mappedBy = "member")
    private List<RuleOfferVote> ruleOfferVotes;

    @Builder
    public Member(Team team, User user, MemberRole role, LocalDateTime createdAt,JoinStatus joinStatus,LocalDateTime dropedAt) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.createdAt = createdAt;
        this.joinStatus = joinStatus;
        this.dropedAt = dropedAt;
    }

    public void expelMember(){
        this.joinStatus = JoinStatus.DROP;
        this.dropedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public Member() {

    }
}
