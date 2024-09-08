package com.example.group_investment.member.dto;

import com.example.group_investment.enums.JoinStatus;
import com.example.group_investment.enums.MemberRole;
import com.example.group_investment.member.Member;
import com.example.group_investment.team.Team;
import com.example.group_investment.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class MemberDto {
    private User user;
    private Team team;
    private MemberRole role;
    private JoinStatus joinStatus;
    private LocalDateTime createdAt;

    @Builder
    public MemberDto(User user, Team team, MemberRole role, JoinStatus joinstatus, LocalDateTime createdAt) {
        this.user = user;
        this.team = team;
        this.role = role;
        this.joinStatus = joinstatus;
        this.createdAt = createdAt;
    }

    public Member toEntity() {
        return Member.builder()
                .user(this.user)
                .team(this.team)
                .role(this.role)
                .joinStatus(this.joinStatus)
                .createdAt(this.createdAt)
                .build();
    }
}
