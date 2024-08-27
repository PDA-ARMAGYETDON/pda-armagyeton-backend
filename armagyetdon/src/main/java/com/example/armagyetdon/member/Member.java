package com.example.armagyetdon.member;

import com.example.armagyetdon.ruleOffer.RuleOffer;
import com.example.armagyetdon.ruleOfferVote.RuleOfferVote;
import com.example.armagyetdon.team.Team;
import com.example.armagyetdon.tradeOffer.TradeOffer;
import com.example.armagyetdon.tradeOfferVote.TradeOfferVote;
import com.example.armagyetdon.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @CreatedDate
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "member")
    private List<TradeOffer> tradeOffers;

    @OneToMany(mappedBy = "member")
    private List<TradeOfferVote> tradeOfferVotes;

    @OneToMany(mappedBy = "member")
    private List<RuleOffer> ruleOffers;

    @OneToMany(mappedBy = "member")
    private List<RuleOfferVote> ruleOfferVotes;
}
