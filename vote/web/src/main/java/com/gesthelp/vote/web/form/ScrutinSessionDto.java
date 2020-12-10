package com.gesthelp.vote.web.form;

import com.gesthelp.vote.service.dto.QuestionDto;

import lombok.Data;

@Data
public class ScrutinSessionDto extends ScrutinVotantDto {
	Integer questionIndex;
	QuestionDto question;
	Boolean allQuestionsAnswered = false;

}
