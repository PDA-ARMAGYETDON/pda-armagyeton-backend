package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Integer> {

    //TODO: 추후 JWT토큰 도입시 대대적으로 수정해야함(userID로 검색 하게)
    @Query("SELECT new com.example.stock_system.transferHistory.dto.AccountTransferDetailDto(" +
            "h.account.id, h.transferAt, h.transferAmt, h.receivingAccountId) " +
            "FROM TransferHistory h LEFT JOIN TeamAccount t ON h.account.id = t.account.id " +
            "WHERE h.account.id = :accountId AND t.account.id IS NULL")
    Optional<AccountTransferDetailDto> findByAccountId(@Param("accountId") int userId);

}
