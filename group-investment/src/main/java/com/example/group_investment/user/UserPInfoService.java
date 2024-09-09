package com.example.group_investment.user;

import com.example.group_investment.user.dto.UpdatePasswordRequest;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPInfoService {

    private final UserPInfoRepository userPInfoRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void savePInfo(User user, String pInfo) {
        UserPInfo userPInfo = UserPInfo.builder()
                .user(user)
                .pInfo(bCryptPasswordEncoder.encode(pInfo))
                .build();

        userPInfoRepository.save(userPInfo);
    }

    public void updatePInfo(int userId, UpdatePasswordRequest request) {
        UserPInfo userPInfo = userPInfoRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        userPInfo.updatePInfo(bCryptPasswordEncoder.encode(request.getPinfo()));
        userPInfoRepository.save(userPInfo);
    }
}
