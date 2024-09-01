package com.example.armagyetdon.rule;

import com.example.armagyetdon.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Integer> {
    Optional<Rule> findByTeam(Team team);
}
