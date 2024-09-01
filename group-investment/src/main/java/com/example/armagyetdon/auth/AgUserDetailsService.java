package com.example.armagyetdon.auth;

import com.example.armagyetdon.user.User;
import com.example.armagyetdon.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AgUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AgUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if (user != null) {
            return new AgUserDetails(user);
        }

        return null;
    }
}
