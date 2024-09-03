package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

    Optional<List<AccountTransferDetailDto>> findByAccountId(int accountId);

}

