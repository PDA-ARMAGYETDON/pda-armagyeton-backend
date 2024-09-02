package com.example.armagyetdon.user;

import com.example.armagyetdon.member.Member;
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

//    private String role;

    @OneToMany(mappedBy = "user")
    private List<Member> members = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserPInfo userPInfo;

}
