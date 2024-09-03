package com.example.armagyetdon.user;

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
}
