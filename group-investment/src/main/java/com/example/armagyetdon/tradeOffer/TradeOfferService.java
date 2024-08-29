package com.example.armagyetdon.tradeOffer;

import com.example.armagyetdon.member.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TradeOfferService {
    private final TradeOfferRepository tradeOfferRepository;
    private final MemberRepository memberRepository;

}
