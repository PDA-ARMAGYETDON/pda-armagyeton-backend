package com.example.group_investment.auth;

import com.example.group_investment.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AgUserDetails implements UserDetails {

    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getMembers().getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getUserPInfo().getPInfo();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    public int getId(){
        return user.getId();
    }

//    public Set<Integer> getTeams() {
////        return null;
//
//        if (user.getMembers().isEmpty()) {
//            return null;
//        }
//        return user
//                .getMembers()
//                .stream()
//                .map(m -> m
//                        .getTeam()
//                        .getId())
//                .collect(Collectors.toSet());
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
