package com.gesthelp.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.ScrutinVoteKey;

public interface ScrutinVoteRepository  extends JpaRepository<ScrutinVote, ScrutinVoteKey>{
	@Query(nativeQuery = true, value = "select count(us.*) from utilisateur_scrutin us, utilisateur u where u.id = us.utilisateur_id and u.role = 'ROLE_VOTANT' and scrutin_id =:scrutin")
	Long nbInscrits(@Param("scrutin") Long scrutin);

	@Query(nativeQuery = true, value = "select count(us.*) from utilisateur_scrutin us, utilisateur u where u.id = us.utilisateur_id and u.role = 'ROLE_VOTANT' and scrutin_id =:scrutin and date_vote is not null")
	Long nbParticipants(@Param("scrutin") Long scrutin);
}
