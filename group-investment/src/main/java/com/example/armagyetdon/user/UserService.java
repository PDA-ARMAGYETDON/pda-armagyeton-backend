package com.example.armagyetdon.user;

import com.example.armagyetdon.user.dto.LoginRequestDto;
import com.example.armagyetdon.user.exception.UserErrorCode;
import com.example.armagyetdon.user.exception.UserException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPInfoRepository userPInfoRepository;

    public void login(LoginRequestDto loginRequest) {

        User foundedUser = userRepository.findByLoginId(loginRequest.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        UserPInfo foundedUserPInfo = userPInfoRepository.findById(foundedUser.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!loginRequest.getPInfo().equals(foundedUserPInfo.getPInfo())) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
