package com.example.group_investment.team;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.team.dto.CreateTeamRequest;
import com.example.group_investment.team.dto.CreateTeamResponse;
import com.example.group_investment.team.dto.TeamDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
//@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    // TeamResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.
    @GetMapping("/{id}")
    public ApiResponse<TeamDto> getTeam(@PathVariable int id) {
        return new ApiResponse<>(200, true, "팀 정보를 조회했습니다.", new TeamDto());
    }

    @PostMapping("/api/users/{id}/groups")
    public ApiResponse<CreateTeamResponse> createTeam(@PathVariable int id, @RequestBody CreateTeamRequest createTeamRequest) {
        CreateTeamResponse createTeamResponse = teamService.createTeam(id, createTeamRequest);
        return new ApiResponse<>(201, true, "팀을 생성했습니다.", createTeamResponse);
    }
}
