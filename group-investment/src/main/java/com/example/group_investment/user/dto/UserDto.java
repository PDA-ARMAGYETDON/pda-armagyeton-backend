package com.example.group_investment.user.dto;

import com.example.group_investment.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private String loginId;
    private String email;
    private String name;
    private String address;
    private String addressDetail;

    public User toEntity(){
        return User.builder()
                .loginId(loginId)
                .email(email)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }
}
