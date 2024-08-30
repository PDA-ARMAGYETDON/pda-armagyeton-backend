package com.example.alarm.firebase;


import com.example.alarm.firebase.dto.FcmTokenRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;


    public void saveFcmToken(FcmTokenRequestDto fcmTokenRequestDto) {
        fcmTokenRepository.save(new FcmToken(fcmTokenRequestDto.getUserId(), fcmTokenRequestDto.getFcmToken()));
    }
//
//    @Transactional
//    public void deleteFcmToken(int userId) {
//        fcmTokenRepository.deleteByUserId(userId);
//    }


}
