package com.example.armagyetdon.member;

import com.example.armagyetdon.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUserIdAndTeamId(int userId, int teamId);
    List<Member> findByTeam(Team team);
    int countByTeam(Team team);
}
