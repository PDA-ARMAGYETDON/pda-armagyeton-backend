package com.example.stock_system.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamAccountRepository extends JpaRepository<TeamAccount, Integer> {

//    TeamAccount findByAccountId(int accountId);

    boolean existsByAccount(Account account);

}
