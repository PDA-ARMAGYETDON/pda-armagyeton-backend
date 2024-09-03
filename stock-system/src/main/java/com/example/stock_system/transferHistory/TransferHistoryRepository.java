package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

    Page<AccountTransferDetailDto> findByAccountId(int accountId, Pageable pageable);

}

