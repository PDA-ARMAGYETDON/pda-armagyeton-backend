package com.example.group_investment.auth;

import com.example.group_investment.user.User;
import com.example.group_investment.user.UserRepository;
import com.example.group_investment.user.UserService;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if (user != null) {
            return new AgUserDetails(user);
        }

        return null;
    }

    @Transactional
    public boolean isTeamExist(int id) {
        return userRepository.findById(id).orElseThrow(()->new UserException(UserErrorCode.USER_NOT_FOUND))
                .getMembers().isEmpty();
    }
}
