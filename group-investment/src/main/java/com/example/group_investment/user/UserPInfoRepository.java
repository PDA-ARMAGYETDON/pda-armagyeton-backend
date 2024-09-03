package com.example.group_investment.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPInfoRepository extends JpaRepository<UserPInfo, Integer> {

}
