package com.example.stock_system.transferHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

    //송금 내역 확인 시
    List<TransferHistory> findAllByAccountId(int accountId);

    //입금 내역 확인 시
    List<TransferHistory> findAllByReceivingAccountId(int receivingAccountId);


}

