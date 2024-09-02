package com.example.group_investment.rule;

import com.example.group_investment.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Integer> {
    Optional<Rule> findByTeam(Team team);
}
