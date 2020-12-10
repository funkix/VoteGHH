package com.gesthelp.vote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gesthelp.vote.domain.QuestionResp;

public interface QuestionRespRepository extends CrudRepository<QuestionResp, Long> {

	@Query("select resp from QuestionResp resp where resp.utilisateurId=?1 and resp.scrutinId=?2 order by resp.question.numquestion")
	List<QuestionResp> userScrutinVotes(Long utilisateurId, Long scrutinId);

}
