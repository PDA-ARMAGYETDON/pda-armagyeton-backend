package com.example.group_investment.team;

import com.example.group_investment.enums.MemberRole;
import com.example.group_investment.enums.TeamStatus;
import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.member.dto.MemberDto;
import com.example.group_investment.member.exception.MemberErrorCode;
import com.example.group_investment.member.exception.MemberException;
import com.example.group_investment.rule.Rule;
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
import java.util.List;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final RuleRepository ruleRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String baseUrl = "http://localhost:8081/";
    private final InvitationRepository invitationRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;


    @Transactional
    public CreateTeamResponse createTeam(CreateTeamRequest createTeamRequest) {
        // 팀을 만든 user
        int userId = 2;

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        //1. 팀
        Team savedTeam;
        TeamDto teamDto = TeamDto.builder()
                .name(createTeamRequest.getName())
                .baseAmt(createTeamRequest.getBaseAmt())
                .headCount(createTeamRequest.getHeadCount())
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
        // 팀장 멤버
        MemberDto memberDto = MemberDto.builder()
                .team(savedTeam)
                .user(user)
                .role(MemberRole.LEADER)
                .build();
        try {
            memberRepository.save(memberDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.MEMBER_SAVE_FAILED);
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

    public InsertCodeTeamResponse insertCode(String inviteCode) {

        Invitation invitation = invitationRepository.findByInviteCode(inviteCode).orElseThrow(()
                -> new TeamException(TeamErrorCode.INVITATION_NOT_FOUND));
        Team team = teamRepository.findById(invitation.getTeam().getId()).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        int invitedMembers = memberRepository.countByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return new InsertCodeTeamResponse(invitation.getTeam().getId(), invitedMembers);
    }

    @Transactional
    public DetailPendingTeamResponse selectPendingDetails() {
        //1. 상세 조회 (모임, 규칙, 인원수)
        int teamId = 1;
        int userId = 1;
        //2-1. 모임 조회
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        TeamDto teamDto = team.fromEntity(team);
        //2-2. 규칙 조회
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        RuleDto ruleDto = rule.fromEntity(rule);
        //2-3. is 모임장
        int isLeader = 0;
        if (memberRepository.findById(userId).orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
                .getRole() == MemberRole.LEADER)
            isLeader = 1;
        //2-4. is 참여
        int isParticipating = 0;
        if (isLeader == 0) {
            List<Member> members = memberRepository.findByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            for (Member member : members) {
                if (member.getUser().getId() == userId) {
                    isParticipating = 1;
                    break;
                }
            }
        }
        //2-5. 인원수 조회
        // FIXME : 멤버
        int invitedMembers = memberRepository.countByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return DetailPendingTeamResponse.builder()
                .name(teamDto.getName())
                .baseAmt(teamDto.getBaseAmt())
                .headCount(teamDto.getHeadCount())
                .category(teamDto.getCategory())
                .startAt(teamDto.getStartAt())
                .endAt(teamDto.getEndAt())
                .prdyVrssRt(ruleDto.getPrdyVrssRt())
                .urgentTradeUpvotes(ruleDto.getUrgentTradeUpvotes())
                .tradeUpvotes(ruleDto.getTradeUpvotes())
                .depositAmt(ruleDto.getDepositAmt())
                .period(ruleDto.getPeriod())
                .payDate(ruleDto.getPayDate())
                .maxLossRt(ruleDto.getMaxLossRt())
                .maxProfitRt(ruleDto.getMaxProfitRt())
                .invitedMembers(invitedMembers)
                .isLeader(isLeader)
                .isParticipating(isParticipating)
                .build();
    }

    public void participateTeam() {
        int teamId = 1;
        int userId = 2;
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        MemberDto memberDto = MemberDto.builder()
                .team(team)
                .user(user)
                .role(MemberRole.MEMBER)
                .build();
        try {
            memberRepository.save(memberDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.MEMBER_SAVE_FAILED);
        }
    }

    public void confirmTeam() {
        int teamId = 3;
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        TeamDto updatedTeamDto = TeamDto.builder()
                .name(team.getName())
                .category(team.getCategory())
                .startAt(team.getStartAt())
                .endAt(team.getEndAt())
                .status(TeamStatus.ACTIVE)
                .build();
        try {
            teamRepository.save(updatedTeamDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.TEAM_SAVE_FAILED);
        }
    }
}
