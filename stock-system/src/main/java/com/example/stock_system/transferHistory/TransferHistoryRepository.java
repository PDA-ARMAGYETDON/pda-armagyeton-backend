package com.example.stock_system.transferHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

}
