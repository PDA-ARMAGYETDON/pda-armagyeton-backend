package com.example.group_investment.team;

import com.example.group_investment.team.dto.*;
import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    // TeamResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.
    @GetMapping("/{id}")
    public ApiResponse<TeamDto> getTeam(@PathVariable int id) {
        return new ApiResponse<>(200, true, "팀 정보를 조회했습니다.", new TeamDto());


    @PostMapping("")
    public ApiResponse<CreateTeamResponse> createTeam(@RequestBody CreateTeamRequest createTeamRequest) {
        CreateTeamResponse createTeamResponse = teamService.createTeam(createTeamRequest);
        return new ApiResponse<>(201, true, "팀을 생성했습니다.", createTeamResponse);
    }
    
    @GetMapping()
    public ApiResponse<InsertCodeTeamResponse> insertCode(@RequestParam(value = "inviteCode", required = true) String inviteCode) {
        InsertCodeTeamResponse insertCodeTeamResponse = teamService.insertCode(inviteCode);
        return new ApiResponse<>(200, true, "초대받은 팀으로 입장합니다",insertCodeTeamResponse);
    }

    @GetMapping("/pending")
    public ApiResponse<DetailPendingTeamResponse> selectDetails() {
        DetailPendingTeamResponse detailPendingTeamResponse = teamService.selectPendingDetails();
        return new ApiResponse<>(200, true, "초대를 받은 팀 정보를 조회했습니다.", detailPendingTeamResponse);
    }
}
