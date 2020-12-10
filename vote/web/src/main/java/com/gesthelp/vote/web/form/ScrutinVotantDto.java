package com.gesthelp.vote.web.form;

import java.util.Date;
import java.util.List;

import com.gesthelp.vote.domain.ScrutinEtat;
import com.gesthelp.vote.service.dto.QuestionDto;

import lombok.Data;

@Data
public class ScrutinVotantDto {
	Long id;
	String nature;
	Date dateOuverture;
	Date dateFermeture;
	ScrutinEtat phase;
	Date dateCreation;
	Date dateVote;
	Integer nbQuestions;
	List<QuestionDto> questions;

}
