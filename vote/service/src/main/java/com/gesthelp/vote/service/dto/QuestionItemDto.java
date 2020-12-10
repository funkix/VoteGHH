package com.gesthelp.vote.service.dto;

import lombok.Data;

@Data
public class QuestionItemDto {
	Long id, questionId;
	String questiontext;
	Boolean isChecked = false;
}
