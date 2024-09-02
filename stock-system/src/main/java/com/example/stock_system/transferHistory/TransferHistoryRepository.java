package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

    @Query("SELECT new com.example.stock_system.transferHistory.dto.AccountTransferDetailDto(" +
            "t.account.id, t.transferAt, t.transferAmt, t.receivingAccountId) " +
            "FROM TransferHistory t " +
            "WHERE t.account.id = :accountId ")
    Optional<List<AccountTransferDetailDto>> findByAccountId(@Param("accountId") int accountId);

}

