package com.example.group_investment.backend_only;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.rule.RuleService;
import com.example.group_investment.rule.dto.CheckDisband;
import com.example.group_investment.team.TeamService;
import com.example.group_investment.team.dto.AutoPayment;
import com.example.group_investment.team.dto.FirstPayment;
import com.example.group_investment.team.dto.PayFail;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/group/backend")
public class BackendController {

    private final TeamService teamService;

    private final RuleService ruleService;

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

    @Operation(summary = "팀에 들어갈때 내야하는 돈",description = "팀 아이디,내야하는 돈, 내야할 사람들 id리스트")
    @GetMapping("/first-payment")
    public ApiResponse createFirstPayment(@RequestParam int teamId){
        FirstPayment firstPayment = teamService.createFirstPayment(teamId);
        return new ApiResponse<>(200,true,"초기에 내야하는 돈",firstPayment);

    }

    @Operation(summary = "팀 id로 유저 이름 찾아오기",description = "채팅창에 띄울 용도")
    @GetMapping("/chat-member")
    public ApiResponse selectMemberNameByTeam(@RequestParam int teamId){
        return new ApiResponse<>(200, true, "유저 들의 이름 list",teamService.selectMemberNameByTeam(teamId));
    }

    @GetMapping("/team-category")
    public ApiResponse getTeamCategory(@RequestParam int teamId){
        return new ApiResponse<>(200,true,"팀 id에 해당하는 카테고리",teamService.getTeamCategory(teamId));

    @GetMapping("/rule-check")
    public ApiResponse getRuleRate(){
        List<CheckDisband> checkDisbands = ruleService.getCheckDisband();
        return new ApiResponse<>(200, true,"완료",checkDisbands);

    }

}
