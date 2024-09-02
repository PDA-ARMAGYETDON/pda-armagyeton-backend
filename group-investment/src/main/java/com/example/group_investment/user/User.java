package com.example.group_investment.user;

import com.example.group_investment.member.Member;
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

    @OneToMany(mappedBy = "user")
    private List<Member> members = new ArrayList<>();
}
