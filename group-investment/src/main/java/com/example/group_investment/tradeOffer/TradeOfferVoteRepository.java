package com.example.group_investment.tradeOffer;

import com.example.group_investment.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeOfferVoteRepository extends JpaRepository<TradeOfferVote, Integer> {
    boolean existsByTradeOfferAndMember(TradeOffer tradeOffer, Member member);
}
