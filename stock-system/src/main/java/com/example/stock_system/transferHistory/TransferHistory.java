package com.example.stock_system.transferHistory;

import com.example.stock_system.account.Account;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
public class TransferHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String senderName;

    @CreatedDate
    private LocalDateTime transferAt;

    private int transferAmt;

    private int receivingAccountId;

    private String receiverName;

    public TransferHistory() {

    }

    public TransferHistory(Account account, String senderName, LocalDateTime transferAt, int transferAmt, int receivingAccountId, String receiverName) {
        this.account = account;
        this.senderName = senderName;
        this.transferAt = transferAt;
        this.transferAmt = transferAmt;
        this.receivingAccountId = receivingAccountId;
        this.receiverName = receiverName;
    }
}
