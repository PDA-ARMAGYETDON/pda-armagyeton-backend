package com.example.armagyetdon.user;

import com.example.armagyetdon.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String loginId;

    @Email
    private String email;

    private String name;

    private String address;

    @OneToOne
    @JoinColumn(name="p_info_id")
    private UserPInfo pInfo;

    @OneToMany(mappedBy = "user")
    private List<Member> members = new ArrayList<>();
}
