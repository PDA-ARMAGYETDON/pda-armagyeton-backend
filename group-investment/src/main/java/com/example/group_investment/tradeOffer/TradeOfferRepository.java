package com.example.group_investment.tradeOffer;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import com.example.group_investment.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeOfferRepository extends JpaRepository<TradeOffer, Integer> {
    Page<TradeOffer> findAllByTeamIdAndTradeType(int memberId, TradeType tradeType, Pageable pageable);

    Page<TradeOffer> findAllByTeamAndOfferStatus(Team team, OfferStatus offerStatus, Pageable pageable);
}
