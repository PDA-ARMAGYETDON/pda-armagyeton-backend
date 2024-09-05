package com.example.stock_system.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountPInfoRepository extends JpaRepository<AccountPInfo, Integer> {
}
