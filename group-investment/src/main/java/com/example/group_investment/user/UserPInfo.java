package com.example.group_investment.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPInfo {
    @Id
    private int id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String pInfo;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
