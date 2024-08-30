package com.example.armagyetdon.team.dto;

import com.example.armagyetdon.enums.Category;
import com.example.armagyetdon.enums.TeamStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamDto {
    private String name;
    private Category category;
    private TeamStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    @Builder
    public TeamDto(String name, Category category, TeamStatus status, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt) {
        this.name = name;
        this.category = category;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }
}
