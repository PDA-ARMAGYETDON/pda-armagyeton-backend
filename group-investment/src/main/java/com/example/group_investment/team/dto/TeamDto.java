package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.TeamStatus;
import com.example.group_investment.team.Team;
import com.example.group_investment.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamDto {
    private User user;
    private String name;
    private Category category;
    private TeamStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    @Builder
    public TeamDto(User user, String name, Category category, TeamStatus status, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt) {
        this.user = user;
        this.name = name;
        this.category = category;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }

    public Team toEntity() {
        return Team.builder()
                .user(this.user)
                .name(this.name)
                .category(this.category)
                .status(this.status)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .createdAt(this.createdAt)
                .build();
    }
}
