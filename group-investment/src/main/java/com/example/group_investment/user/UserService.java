package com.example.group_investment.user;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;
import com.example.group_investment.member.MemberRepository;
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

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPInfoService userPInfoService;
    private final MemberRepository memberRepository;
    private final RabbitTemplate rabbitTemplate;

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
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        if (checkEmail(request.getEmail()) != null){
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

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
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        });
    }

    public User checkEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_ALREADY_EXISTS));
    }

    public UpdateResponse updateUser(int userId, UpdateRequest request) {
        if (checkEmail(request.getEmail()).getId() != userId) {
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
}
