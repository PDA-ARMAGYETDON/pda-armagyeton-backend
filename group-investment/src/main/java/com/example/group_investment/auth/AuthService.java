package com.example.group_investment.auth;

import com.example.common.auth.JwtUtil;
import com.example.group_investment.auth.exception.AuthoErrorCode;
import com.example.group_investment.auth.exception.AuthoException;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.user.User;
import com.example.group_investment.user.UserRepository;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public String updateToken(int jwtUserId, int jwtTeamId, int teamId, String jwtToken) {

        if (jwtTeamId == teamId) {
            return jwtToken;
        }

        User user = userRepository.findById(jwtUserId).orElseThrow(()->new UserException(UserErrorCode.USER_NOT_FOUND));

        // 팀에 속해있는지 확인
        if (!memberRepository.existsByUserAndTeamId(user, teamId)) {
            throw new AuthoException(AuthoErrorCode.NOT_TEAM_MEMBER);
        }

        return jwtUtil.createJwt(jwtUserId, teamId, user.containTeam());
    }
}
