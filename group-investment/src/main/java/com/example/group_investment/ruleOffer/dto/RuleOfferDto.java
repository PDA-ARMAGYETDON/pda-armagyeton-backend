package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.RuleOffer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleOfferDto {
    private Rule rule;
    private Member member;
    private int upvotes;
    private int downvotes;
    private int totalvotes;
    private RuleType rtype;
    private OfferStatus status;

    @Builder
    public RuleOfferDto(Rule rule, Member member, int upvotes, int downvotes, int totalvotes, RuleType rtype, OfferStatus status) {
        this.rule = rule;
        this.member = member;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.rtype = rtype;
        this.status = status;
    }

    public RuleOffer toEntity() {
        return RuleOffer.builder()
                .rule(this.rule)
                .member(this.member)
                .upvotes(this.upvotes)
                .downvotes(this.downvotes)
                .totalvotes(this.totalvotes)
                .rtype(this.rtype)
                .status(this.status).build();
    }
}
