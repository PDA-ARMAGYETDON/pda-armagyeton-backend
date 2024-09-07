package com.example.group_investment.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    Optional<Invitation> findByInviteCode(String inviteCode);

    Optional<Invitation> findByTeam(Team team);
}
