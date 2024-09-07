package com.example.group_investment.team;

import com.example.group_investment.team.dto.*;
import com.example.group_investment.team.dto.CreateTeamRequest;
import com.example.group_investment.team.dto.CreateTeamResponse;

import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    // TeamResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.
    @Operation(summary = "유저가 속한 팀들의 pk와 status를 조회하는 API")
    @GetMapping("/users")
    public ApiResponse<List<TeamByUserResponse>> selectTeamByUser(@RequestAttribute("userId") int userId) {
        List<TeamByUserResponse> teamByUserResponse = teamService.selectTeamByUser(userId);
        return new ApiResponse<>(200, true, "유저가 속한 팀의 정보를 조회했습니다.", teamByUserResponse);
    }

    @Operation(summary = "팀의 모임 원칙 조회하는 API")
    @GetMapping("/rules")
    public ApiResponse<DetailTeamResponse> selectTeamRules(@RequestAttribute("teamId") int teamId) {
        DetailTeamResponse detailTeamResponse = teamService.selectTeamRules(teamId);
        return new ApiResponse<>(200, true, "팀의 모임 원칙을 조회했습니다.",detailTeamResponse);
    }

    @Operation(summary = "팀을 생성하는 API")
    @PostMapping("")
    public ApiResponse<CreateTeamResponse> createTeam(@RequestAttribute("userId") int userId, @RequestBody CreateTeamRequest createTeamRequest) {
        CreateTeamResponse createTeamResponse = teamService.createTeam(userId, createTeamRequest);
        return new ApiResponse<>(201, true, "팀을 생성했습니다.", createTeamResponse);
    }


    @Operation(summary = "초대 코드 입력 API")
    @GetMapping()
    public ApiResponse<InsertCodeTeamResponse> insertCode(@RequestParam(value = "inviteCode", required = true) String inviteCode) {
        InsertCodeTeamResponse insertCodeTeamResponse = teamService.insertCode(inviteCode);
        return new ApiResponse<>(200, true, "초대받은 팀으로 입장합니다",insertCodeTeamResponse);
    }

    @Operation(summary = "PENDING 상태 팀을 조회하는 API")
    @GetMapping("/pending")
    public ApiResponse<DetailPendingTeamResponse> selectDetails(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId) {
        DetailPendingTeamResponse detailPendingTeamResponse = teamService.selectPendingDetails(userId, teamId);
        return new ApiResponse<>(200, true, "초대를 받은 팀 정보를 조회했습니다.", detailPendingTeamResponse);
    }

    @Operation(summary = "팀을 참가하는 API")
    @GetMapping("/{id}/participate")
    public ApiResponse participateTeam(@RequestAttribute("userId") int userId, @PathVariable int id) {
        teamService.participateTeam(userId, id);
        return new ApiResponse<>(201, true, "모임에 참가했습니다.", null);
    }

    @Operation(summary = "팀을 확정하는 API")
    @PutMapping()
    public ApiResponse confirmTeam(@RequestAttribute("teamId") int teamId) {
        teamService.confirmTeam(teamId);
        return new ApiResponse<>(200, true, "모임을 확정했습니다.", null);
    }
    @Operation(summary = "자동 이체 데이터 증권시스템으로 전달",description = "자동이체를 처리해야 하는 팀ID,금액,모임을 사용중인 멤버를 반환")
    @PostMapping("/autoPayment")
    public ApiResponse getAutoPaymentGroups(){
        List<AutoPayment> autoPayments = teamService.autoPayments();
        return new ApiResponse<>(200,true,"완료",autoPayments);
    }

    @Operation(summary = "멤버 방출",description = "돈을 안낸 멤버를 방출한다.")
    @PostMapping("/expelMember")
    public ApiResponse expelMember(@RequestBody List<PayFail> payFails){
        teamService.expelMember(payFails);
        return new ApiResponse<>(200,true,"완료",null);
    }

    @Operation(summary = "팀 id로 멤버찾기",description = "돈 다시 돌려줄때 teamId로 멤버 리스트 찾음")
    @GetMapping("/member")
    public ApiResponse selectMemberByTeam(@RequestParam int teamId){
        return new ApiResponse<>(200,true,"해당 팀 id의 유저 리스트",teamService.selectMemberByTeam(teamId));
    }

}

