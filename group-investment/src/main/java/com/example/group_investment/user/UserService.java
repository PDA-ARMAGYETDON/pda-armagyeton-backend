package com.example.group_investment.user;

import com.example.group_investment.member.Member;
import com.example.group_investment.user.dto.GetUserResponse;
import com.example.group_investment.user.dto.SignInRequest;
import com.example.group_investment.user.dto.SignInResponse;
import com.example.group_investment.user.dto.SignUpRequest;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPInfoService userPInfoService;

    public GetUserResponse get(int id) {
        return userRepository.findById(id)
                .map(user -> GetUserResponse.builder()
                        .loginId(user.getLoginId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .address(user.getAddress())
                        .build())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    public void signUp(SignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User createdUser = User.builder()
                .loginId(request.getLoginId())
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();

        userRepository.save(createdUser);

        // pInfo 저장
        userPInfoService.savePInfo(createdUser, request.getPassword());

    }


//    public SignInResponse signIn(SignInRequest request) {
//        User user = userRepository.findByLoginId(request.getLoginId())
//                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
//
//        if (!userPInfoService.matchPInfo(user, request.getPassword())) {
//            throw new UserException(UserErrorCode.LOGIN_FAILED);
//        }
//
//        return SignInResponse.builder()
//                .accessToken("access token")
//                .build();
//    }
}
