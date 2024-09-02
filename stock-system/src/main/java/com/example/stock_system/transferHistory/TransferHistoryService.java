package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import com.example.stock_system.transferHistory.exception.TransferHistoryErrorCode;
import com.example.stock_system.transferHistory.exception.TransferHistoryException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferHistoryService {

    private final TransferHistoryRepository transferHistoryRepository;

    public AccountTransferDetailDto getAccountTransferDetail(int userId) {
        return transferHistoryRepository
                .findByAccountId(userId).orElseThrow(
                        () -> new TransferHistoryException(TransferHistoryErrorCode.TRANSFER_HISTORY_NOT_FOUND)
                );
    }

}
