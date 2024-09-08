package com.example.stock_system.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamAccountRepository extends JpaRepository<TeamAccount, Integer> {

    Optional<TeamAccount> findByTeamId(int teamId);

    Optional<TeamAccount> findByAccountId(int accountId);

    Optional<TeamAccount> findByAccount(Account account);
}
