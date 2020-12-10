package com.gesthelp.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.ScrutinVoteKey;

public interface ScrutinVoteRepository  extends JpaRepository<ScrutinVote, ScrutinVoteKey>{

}
