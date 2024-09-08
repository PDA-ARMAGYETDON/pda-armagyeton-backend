package com.example.stock_system.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<List<Account>> findByUserId(int userId);

    Optional<Account> findAllByUserIdAndAccountNumberContaining(int userId, String accountType);

    Optional<List<Account>> findByAccountNumberStartingWith(String number);
}
