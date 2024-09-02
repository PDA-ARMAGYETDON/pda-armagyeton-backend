package com.example.group_investment.team;

import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.rule.RuleRepository;
import com.example.group_investment.rule.dto.RuleDto;
import com.example.group_investment.rule.exception.RuleErrorCode;
import com.example.group_investment.rule.exception.RuleException;
import com.example.group_investment.team.dto.*;
import com.example.group_investment.team.exception.TeamErrorCode;
import com.example.group_investment.team.exception.TeamException;
import com.example.group_investment.user.User;
import com.example.group_investment.user.UserRepository;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final RuleRepository ruleRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String baseUrl = "http://localhost:8081/";
    private final InvitationRepository invitationRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;


    @Transactional
    public CreateTeamResponse createTeam(int userId, CreateTeamRequest createTeamRequest) {
        // 팀을 만든 user
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        //1. 팀
        Team savedTeam;
        TeamDto teamDto = TeamDto.builder()
                .user(user)
                .name(createTeamRequest.getName())
                .category(createTeamRequest.getCategory())
                .startAt(createTeamRequest.getStartAt())
                .endAt(createTeamRequest.getEndAt())
                .build();
        try {
            savedTeam = teamRepository.save(teamDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.TEAM_SAVE_FAILED);
        }
        //2. 규칙
        RuleDto ruleDto = RuleDto.builder()
                .team(savedTeam)
                .prdyVrssRt(createTeamRequest.getPrdyVrssRt())
                .urgentTradeUpvotes(createTeamRequest.getUrgentTradeUpvotes())
                .tradeUpvotes(createTeamRequest.getTradeUpvotes())
                .depositAmt(createTeamRequest.getDepositAmt())
                .period(createTeamRequest.getPeriod())
                .payDate(createTeamRequest.getPayDate())
                .maxLossRt(createTeamRequest.getMaxLossRt())
                .maxProfitRt(createTeamRequest.getMaxProfitRt())
                .build();
        try {
            ruleRepository.save(ruleDto.toEntity());
        } catch (Exception e) {
            throw new RuleException(RuleErrorCode.RULE_SAVE_FAILED);
        }
        //3 초대코드 생성
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        String inviteCode = code.toString();

        //4 초대링크 생성
        String inviteUrl = baseUrl + inviteCode;

        //5 초대정보 저장
        InvitationDto invitationDto = InvitationDto.builder()
                .team(savedTeam)
                .inviteCode(inviteCode)
                .inviteUrl(inviteUrl)
                .build();
        try {
            invitationRepository.save(invitationDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.INVITATION_SAVE_FAILED);
        }
        return new CreateTeamResponse(inviteCode, inviteUrl);
    }

//    public SelectTeamResponse selectTeam(int id) {
//        SelectTeamResponse selectTeamResponse = teamRepository.findById(id);
//        return selectTeamResponse;
//    }
}
