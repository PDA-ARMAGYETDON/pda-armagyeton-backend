package com.example.group_investment.backend_only;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.team.TeamService;
import com.example.group_investment.team.dto.AutoPayment;
import com.example.group_investment.team.dto.PayFail;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/backend")
public class BackendController {

    private final TeamService teamService;

    @Operation(summary = "자동 이체 데이터 증권시스템으로 전달",description = "자동이체를 처리해야 하는 팀ID,금액,모임을 사용중인 멤버를 반환")
    @PostMapping("/auto-payment")
    public ApiResponse getAutoPaymentGroups(){
        List<AutoPayment> autoPayments = teamService.autoPayments();
        return new ApiResponse<>(200,true,"완료",autoPayments);
    }

    @Operation(summary = "멤버 방출",description = "돈을 안낸 멤버를 방출한다.")
    @PostMapping("/expel-member")
    public ApiResponse expelMember(@RequestBody List<PayFail> payFails){
        teamService.cancelMember(payFails);
        return new ApiResponse<>(200,true,"완료",null);
    }

    @Operation(summary = "팀 id로 멤버찾기",description = "teamId로 멤버들 user_id반환")
    @GetMapping("/member")
    public ApiResponse selectMemberByTeam(@RequestParam int teamId){
        return new ApiResponse<>(200,true,"해당 팀 id의 유저 리스트",teamService.selectMemberByTeam(teamId));
    }

    @Operation(summary = "팀 id로 유저 이름 찾아오기",description = "채팅창에 띄울 용도")
    @GetMapping("/chatt-member")
    public ApiResponse selectMemberNameByTeam(@RequestParam int teamId){
        return new ApiResponse<>(200, true, "유저 들의 이름 list",teamService.selectMemberNameByTeam(teamId));
    }

}
