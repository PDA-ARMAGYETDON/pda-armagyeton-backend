package com.example.group_investment.user;

import com.example.group_investment.member.Member;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.team.Team;
import com.example.group_investment.team.TeamRepository;
import com.example.group_investment.team.TeamService;
import com.example.group_investment.user.dto.*;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPInfoService userPInfoService;
    private final TeamService teamService;
    private final MemberRepository memberRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TeamRepository teamRepository;

    public GetUserResponse get(int jwtUserId, int id) {
        if (jwtUserId != id) {
            throw new UserException(UserErrorCode.FORBIDDEN_ERROR);
        }

        return userRepository.findById(id)
                .map(user -> GetUserResponse.builder()
                        .loginId(user.getLoginId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .address(user.getAddress())
                        .addressDetail(user.getAddressDetail())
                        .build())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    public void signUp(SignUpRequest request) {
        checkId(request.getLoginId());
        checkEmail(request.getEmail());

        User createdUser = User.builder()
                .loginId(request.getLoginId())
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .build();

        userRepository.save(createdUser);

        // pInfo 저장
        userPInfoService.savePInfo(createdUser, request.getPassword());

    }

    public void saveFcmToken(FcmTokenRequestDto fcmTokenRequestDto) {
        try {

            ArrayList<Integer> teamList = new ArrayList<>();

            memberRepository.findAllByUserId(fcmTokenRequestDto.getUserId()).orElseThrow(
                    () -> new UserException(UserErrorCode.USER_NOT_FOUND)
            ).forEach(member -> teamList.add(member.getTeam().getId()));

            FcmTokenResponseDto data = new FcmTokenResponseDto(fcmTokenRequestDto.getUserId(), teamList, fcmTokenRequestDto.getFcmToken());

            //MQ 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String objToJson = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend("main_to_alarm", objToJson);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Object -> Json convert Error");
        } catch (AmqpException e) {
            e.printStackTrace();
            log.error("RabbitMQ Error");
        }

    }

    public void checkId(String loginId) {
        userRepository.findByLoginId(loginId).ifPresent(user -> {
            throw new UserException(UserErrorCode.LOGIN_ID_ALREADY_EXISTS);
        });
    }

    public void checkEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        });
    }

    public boolean userWithEmailAlreadyExist(String email, int userId) {
        // 이메일로 유저 아이디 찾기
        // 없으면 false
        // 있으면 userId와 같은지 확인해서 같으면 false, 다르면 true
        return userRepository.findByEmail(email)
                .map(user -> user.getId() != userId)
                .orElse(false);
    }

    public UpdateResponse updateUser(int userId, UpdateRequest request) {
        // 이메일 중복 체크해서 다른 유저가 사용하고 있는 이메일이면 에러
        if (userWithEmailAlreadyExist(request.getEmail(), userId)) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.updateUserInfo(request.getEmail(), request.getName(), request.getAddress(), request.getAddressDetail());

        userRepository.save(user);

        return UpdateResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .addressDetail(user.getAddressDetail())
                .build();
    }

    public void deleteUser(int userId, int teamId) {
        /* 유저 처리
        * 유저 상태 변경 "삭제" 처리
        * 유저 상태 변경 "탈퇴"
        * */
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.delete();
        log.info("유저가 탈퇴 처리되었습니다.");
        /* 팀 처리
        *  1. 내가 가진 팀 조회
        *       팀이 없으면 종료
        *  2. 팀이 pending 상태 && 그 팀의 팀장이라면
        *       팀 삭제
        *  3. 팀 pending 상태인데 팀장이 아니거나 || 팀장이든 아니든 팀이 ACTIVE 상태라면
        *       팀 탈퇴
        * */
        List<Member> members = memberRepository.findAllByUserId(userId).orElse(new ArrayList<>());
        if (members.isEmpty()) {
            return;
        }
        log.info("참여하고 있는 팀이 있습니다.");

        for (Member member : members) {
            Team currentTeam = member.getTeam();
            if (currentTeam.isPending() && teamService.isTeamLeader(member)) {
                System.out.println("펜딩 팀의 팀장이니깐 팀 삭제하겠음");
                teamService.cancelTeam(currentTeam);
            } else {
                System.out.println("펜딩 팀이 아니거나, 팀장이 아님. 팀 탈퇴하겠음");
                teamService.cancelMember(userId, currentTeam.getId());
            }
        }
    }
}
