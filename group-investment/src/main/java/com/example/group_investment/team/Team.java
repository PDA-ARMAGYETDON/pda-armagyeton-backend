package com.example.group_investment.team;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.TeamStatus;
import com.example.group_investment.member.Member;
import com.example.group_investment.team.dto.TeamDto;
import com.example.group_investment.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    private int baseAmt;
    private int headCount;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();


    public int getSizeOfMembers() {
        return members.size();
    }

    @Builder
    public Team(String name, int baseAmt, int headCount, Category category, TeamStatus status, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt) {
        this.name = name;
        this.baseAmt = baseAmt;
        this.headCount = headCount;
        this.category = category;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
    }

    public Team() {

    }

    public TeamDto fromEntity(Team team) {
        return TeamDto.builder()
                .name(team.getName())
                .baseAmt(team.getBaseAmt())
                .headCount(team.getHeadCount())
                .category(team.getCategory())
                .status(team.getStatus())
                .startAt(team.getStartAt())
                .endAt(team.getEndAt())
                .createdAt(team.getCreatedAt())
                .build();

    }
}
