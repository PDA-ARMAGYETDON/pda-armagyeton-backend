package com.example.group_investment.ruleOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "RTYPE")
public class RuleOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Rule.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

//    private RuleType rule_type;

    @Builder.Default
    private int upvotes = 0;
    @Builder.Default
    private int downvotes = 0;

    private int totalvotes;

    @Formula("RTYPE")
    private String rtype;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.PROGRESS;
}
