package com.gesthelp.vote.web.form;

import java.util.List;

import com.gesthelp.vote.service.dto.QuestionDto;

import lombok.Data;

@Data
public class ScrutinQuestionsDto {
	Long scrutinId;
	String scrutinText;
	List<QuestionDto> questions;
}
