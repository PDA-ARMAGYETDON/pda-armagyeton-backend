package com.example.armagyetdon.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
public class UserPInfo {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String pInfo;

    @UpdateTimestamp
    LocalDateTime updatedAt;

}
