package com.example.stock_system.stocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Integer> {

    Optional<Stocks> findByCode(String code);

    Optional<Stocks> findByName(String name);
}
