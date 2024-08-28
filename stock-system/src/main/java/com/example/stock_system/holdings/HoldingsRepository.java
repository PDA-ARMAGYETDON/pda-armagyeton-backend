package com.example.stock_system.holdings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoldingsRepository extends JpaRepository<Holdings, Integer> {
}
