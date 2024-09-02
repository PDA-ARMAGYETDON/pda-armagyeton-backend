package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.TradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeOfferRepository extends JpaRepository<TradeOffer, Integer> {
    Optional<List<TradeOffer>> findAllByTeamIdAndTradeType(int memberId, TradeType tradeType);
}
