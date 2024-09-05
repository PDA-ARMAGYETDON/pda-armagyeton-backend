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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public String updateToken(String jwtToken, int teamId) {

        int currentUserId = jwtUtil.getUserId(jwtToken);
        User user = userRepository.findById(currentUserId).orElseThrow(()->new UserException(UserErrorCode.USER_NOT_FOUND));

        int currentTeamId = 0;
        // jwt에 teamId가 있는 경우
        if (jwtUtil.containsTeam(jwtToken)) {
            currentTeamId = jwtUtil.getTeamId(jwtToken);
        }

        // 팀에 속해있는지 확인
        if (!memberRepository.existsByUserAndTeamId(user, teamId)) {
            throw new AuthoException(AuthoErrorCode.NOT_TEAM_MEMBER);
        }

        String newJwtToken = jwtToken;
        if (teamId != currentTeamId) {
            newJwtToken = jwtUtil.createJwt(currentUserId, teamId, jwtUtil.isTeamExist(jwtToken));
        }

        return newJwtToken;
    }
}
