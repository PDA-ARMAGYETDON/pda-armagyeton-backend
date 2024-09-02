package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.TeamStatus;
import com.example.group_investment.team.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TeamDto {
    private String name;
    private Category category;
    private TeamStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    @Builder
    public TeamDto(String name, Category category, TeamStatus status, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt) {

        this.name = name;
        this.category = category;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }

    public Team toEntity() {
        return Team.builder()
                .name(this.name)
                .category(this.category)
                .status(this.status)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .createdAt(this.createdAt)
                .build();
    }

}
