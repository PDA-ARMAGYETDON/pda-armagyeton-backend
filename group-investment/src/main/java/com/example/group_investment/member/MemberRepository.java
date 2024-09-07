package com.example.group_investment.member;

import com.example.group_investment.team.Team;
import com.example.group_investment.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUserIdAndTeamId(int userId, int teamId);

    Optional<List<Member>> findByTeam(Team team);

    Optional<Integer> countByTeam(Team team);

    boolean existsByUserAndTeamId(User user, int teamId);

    Optional<List<Member>> findByUser(User user);

    Optional<List<Member>> findAllByUserId(int userId);

    Optional<List<Member>> findByTeamId(int teamId);
}
