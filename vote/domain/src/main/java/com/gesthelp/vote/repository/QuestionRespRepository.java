package com.gesthelp.vote.repository;

import org.springframework.data.repository.CrudRepository;

import com.gesthelp.vote.domain.QuestionResp;

public interface QuestionRespRepository  extends CrudRepository<QuestionResp, Long> {

}
