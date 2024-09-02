package com.example.stock_system.transferHistory;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import com.example.stock_system.transferHistory.exception.TransferHistoryErrorCode;
import com.example.stock_system.transferHistory.exception.TransferHistoryException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TransferHistoryService {

    private static final Logger log = LoggerFactory.getLogger(TransferHistoryService.class);
    private final TransferHistoryRepository transferHistoryRepository;
    private final AccountRepository accountRepository;
    private final TeamAccountRepository teamAccountRepository;

    @Transactional
    public List<AccountTransferDetailDto> getAccountTransferDetail(int userId) {
        List<AccountTransferDetailDto> foundedTransferDetailList = null;

        List<Account> foundedAccount = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.USER_ACCOUNT_NOT_FOUND));

        //TODO: 성능 개선 필요
        for (Account account : foundedAccount) {
            if (!teamAccountRepository.existsByAccount(account)) {
                foundedTransferDetailList =
                        transferHistoryRepository.findByAccountId(account.getId())
                                .orElseThrow(() -> new TransferHistoryException(TransferHistoryErrorCode.TRANSFER_HISTORY_NOT_FOUND));
            }
        }
        return foundedTransferDetailList;
    }

}