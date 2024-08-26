package com.example.armagyetdon.team.dto;

import com.example.armagyetdon.team.Category;
import com.example.armagyetdon.team.TeamStatus;

import java.time.LocalDateTime;

public class TeamDto {
    private String name;
    private Category category;
    private TeamStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
