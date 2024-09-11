package com.example.group_investment.team;

import com.example.group_investment.auth.AuthService;
import com.example.group_investment.enums.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    @Value("${tg.url}")
    private static String AG_URL;

    private final TeamRepository teamRepository;
    private final RuleRepository ruleRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String baseUrl = AG_URL + "/";
    private final InvitationRepository invitationRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AuthService authService;


    @Transactional
    public CreateTeamResponse createTeam(int userId, CreateTeamRequest createTeamRequest,
                                         int teamId, String jwtToken) {
        // 팀을 만든 user
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
                .status(TeamStatus.PENDING)
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
                .joinstatus(JoinStatus.ACTIVE)
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
                .build();
        try {
            invitationRepository.save(invitationDto.toEntity());
        } catch (Exception e) {
            throw new TeamException(TeamErrorCode.INVITATION_SAVE_FAILED);
        }

        //6 토큰 업데이트
        String updatedJwtToken = authService.updateToken(userId, teamId, savedTeam.getId(), jwtToken);

        return new CreateTeamResponse(inviteCode, inviteUrl, updatedJwtToken);
    }

    public InsertCodeTeamResponse insertCode(String inviteCode) {

        Invitation invitation = invitationRepository.findByInviteCode(inviteCode).orElseThrow(()
                -> new TeamException(TeamErrorCode.INVITATION_NOT_FOUND));
        Team team = teamRepository.findById(invitation.getTeam().getId()).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        int invitedMembers = memberRepository.countByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return new InsertCodeTeamResponse(invitation.getTeam().getId(), invitedMembers);
    }

    @Transactional
    public DetailPendingTeamResponse selectPendingDetails(int userId, int teamId) {
        //1. 상세 조회 (모임, 규칙, 인원수)
        //2-1. 모임 조회
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        TeamDto teamDto = team.fromEntity(team);
        //2-2. 규칙 조회
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        RuleDto ruleDto = rule.fromEntity(rule);
        //2-3. is 모임장
        int isLeader = 0;
        
        List<Member> joinedMembers = memberRepository.findByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        for (Member member : joinedMembers) {
            if (member.getUser().getId() == userId) {
                if (member.getRole()==MemberRole.LEADER)
                    isLeader = 1;
            }
        }
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
        int invitedMembers = memberRepository.countByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        //2-6. 팀의 초대 코드 조회
        Invitation invitation = invitationRepository.findByTeam(team).orElseThrow(() -> new TeamException(TeamErrorCode.INVITATION_NOT_FOUND));
        String invitedCode = invitation.getInviteCode();

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
                .invitedCode(invitedCode)
                .build();
    }

    public ParticipateResponse participateTeam(int userId, int teamId, int jwtTeamId, String jwtToken) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        MemberDto memberDto = MemberDto.builder()
                .team(team)
                .user(user)
                .role(MemberRole.MEMBER)
                .joinstatus(JoinStatus.ACTIVE)
                .build();
        try {
            memberRepository.save(memberDto.toEntity());
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.MEMBER_SAVE_FAILED);
        }

        // 토큰 발급
        String updatedJwtToken = authService.updateToken(userId, jwtTeamId, teamId, jwtToken);
        return ParticipateResponse.builder()
                .updatedToken(updatedJwtToken)
                .build();
    }

    public void confirmTeam(int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        int confirmMembers = memberRepository.countByTeam(team).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Team updatedTeam = team.toBuilder()
                .status(TeamStatus.ACTIVE)
                .headCount(confirmMembers)
                .build();
        teamRepository.save(updatedTeam);
    }

    public DetailTeamResponse selectTeamRules(int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        Rule rule = ruleRepository.findByTeam(team).orElseThrow(() -> new RuleException(RuleErrorCode.RULE_NOT_FOUND));
        return DetailTeamResponse.builder()
                .startAt(team.getStartAt())
                .endAt(team.getEndAt())
                .baseAmt(team.getBaseAmt())
                .prdyVrssRt(rule.getPrdyVrssRt())
                .urgentTradeUpvotes(rule.getUrgentTradeUpvotes())
                .tradeUpvotes(rule.getTradeUpvotes())
                .depositAmt(rule.getDepositAmt())
                .period(rule.getPeriod())
                .payDate(rule.getPayDate())
                .maxLossRt(rule.getMaxLossRt())
                .maxProfitRt(rule.getMaxProfitRt())
                .build();

    }

    public List<TeamByUserResponse> selectTeamByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        List<Member> members = memberRepository.findByUser(user).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<TeamByUserResponse> teamByUserResponses = new ArrayList<>();
        for (Member member : members) {
            if (member.getJoinStatus() == JoinStatus.ACTIVE)
                teamByUserResponses.add(new TeamByUserResponse(member.getTeam().getId(), member.getTeam().getStatus(), member.getTeam().getName(),member.getTeam().getCategory()));
        }
        return teamByUserResponses;
    }


    public List<AutoPayment> autoPayments() {
        List<Rule> rules = ruleRepository.findAll();
        LocalDate today = LocalDate.now();

        List<AutoPayment> autoPayments = new ArrayList<>();

        for (Rule rule : rules) {
            LocalDate payDate = rule.getPayDate();
            RulePeriod period = rule.getPeriod();
            Team team = rule.getTeam();

            Team findTeam = teamRepository.findById(team.getId()).orElseThrow(()->new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

            boolean isPayDate = false;


            if (!payDate.isAfter(today)&&team.getEndAt().isAfter(LocalDateTime.now())&&findTeam.getStatus()!= TeamStatus.FINISHED) {
                if (period == RulePeriod.WEEK) {
                    if (payDate.getDayOfWeek() == today.getDayOfWeek()) {
                        isPayDate = true;
                    }
                } else if (period == RulePeriod.MONTH) {
                    if (payDate.getDayOfMonth() == today.getDayOfMonth()) {
                        isPayDate = true;
                    }
                }
            }


            if (isPayDate) {
                List<Member> members = memberRepository.findByTeam(team)
                        .orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));


                List<Integer> userIds = new ArrayList<>();

                members.stream()
                        .filter(member -> member.getJoinStatus() == JoinStatus.ACTIVE)
                        .forEach(member -> userIds.add(member.getUser().getId()));

                if (!userIds.isEmpty()) {
                    AutoPayment autoPayment = new AutoPayment(team.getId(), rule.getDepositAmt(), userIds);
                    autoPayments.add(autoPayment);
                }

            }
        }

        return autoPayments;
    }

    public void cancelMember(List<PayFail> payFails) {

        for (PayFail payFail : payFails) {
            int teamId = payFail.getTeamId();
            List<Integer> userIds = payFail.getUserId();

            for (Integer userId : userIds) {
                Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                        .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
                member.expelMember();
                memberRepository.save(member);
            }
        }
    }

    public void cancelMember(int userId, int teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.cancelMember();
        memberRepository.save(member);
    }

    public List<Integer> selectMemberByTeam(int teamId) {
        List<Member> members = memberRepository.findByTeamId(teamId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return members.stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());
    }

    public List<String> selectMemberNameByTeam(int teamId) {
        List<Member> members = memberRepository.findByTeamId(teamId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return members.stream()
                .map(member -> member.getUser().getName())
                .collect(Collectors.toList());
    }


    public FirstPayment createFirstPayment(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        List<Member> members = memberRepository.findByTeamId(teamId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Integer> userIds = members.stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());

        int paymentMoney = team.getBaseAmt();

        return new FirstPayment(teamId, paymentMoney, userIds);
    }



    public boolean isTeamLeader(Member member) {
        return member.getRole() == MemberRole.LEADER;
    }

    public void cancelTeam(Team team) {
        // 팀 상태 CANCEL로 변경
        team.cancelTeam();
        teamRepository.save(team);

        // 멤버 모두 방출
        List<Member> members = memberRepository.findByTeam(team).orElse(new ArrayList<>());
        for(Member member : members){
            member.cancelMember();
            memberRepository.save(member);
        }
    }


    public Category getTeamCategory(int teamId){
        Team findteam = teamRepository.findById(teamId).orElseThrow(()->new TeamException(TeamErrorCode.TEAM_NOT_FOUND));
        return findteam.getCategory();
    }

    public List<Integer> getFinishTeam(){
        LocalDate today = LocalDate.now();

        List<Team> teamsToFinish = teamRepository.findAll().stream()
                .filter(team -> team.getEndAt().toLocalDate().equals(today))
                .toList();

        teamsToFinish.forEach(team -> {
            team.finishTeam();
            teamRepository.save(team);
        });

        return teamsToFinish.stream()
                .map(Team::getId) // 팀의 ID 추출
                .collect(Collectors.toList());
    }
    public String selectUserName(int userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new UserException(UserErrorCode.USER_NOT_FOUND));
        String name = user.getName();
        return name;
    }


}




