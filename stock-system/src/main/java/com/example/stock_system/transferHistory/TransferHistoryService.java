package com.example.stock_system.transferHistory;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import com.example.stock_system.transferHistory.exception.TransferHistoryErrorCode;
import com.example.stock_system.transferHistory.exception.TransferHistoryException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransferHistoryService {

    private final TransferHistoryRepository transferHistoryRepository;
    private final AccountRepository accountRepository;

    private final String PRIVATE_ACCOUNT = "81901";
    private final String TEAM_ACCOUNT = "81902";

    @Transactional
    public List<AccountTransferDetailDto> getPrivateAccountTransferDetail(int userId) {

        Account foundedPrivateAccount = accountRepository
                .findByUserIdAndAccountNumberContaining(userId, PRIVATE_ACCOUNT)
                .orElseThrow(() -> new AccountException(
                        AccountErrorCode.PRIVATE_ACCOUNT_NOT_FOUND));

        List<AccountTransferDetailDto> foundedTransferDetailList = transferHistoryRepository
                .findByAccountId(foundedPrivateAccount.getId())
                .orElseThrow(() -> new TransferHistoryException(
                        TransferHistoryErrorCode.TRANSFER_HISTORY_NOT_FOUND));

        return foundedTransferDetailList;

    }

}