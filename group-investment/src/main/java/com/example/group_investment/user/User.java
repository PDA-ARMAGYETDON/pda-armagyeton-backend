package com.example.group_investment.user;

import com.example.group_investment.enums.UserStatus;
import com.example.group_investment.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String loginId;

    @Email
    private String email;

    private String name;

    private String address;
    private String addressDetail;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user")
    private List<Member> members;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserPInfo userPInfo;

    public Boolean containTeam(){
        return !members.isEmpty();
    }

    public void updateUserInfo(String email, String name, String address, String addressDetail) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public void delete() {
        this.status = UserStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
