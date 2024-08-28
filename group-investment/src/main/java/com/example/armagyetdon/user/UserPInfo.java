package com.example.armagyetdon.user;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class UserPInfo {
    @Id
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    private String pInfo;

    @UpdateTimestamp
    LocalDateTime updatedAt;

}
