package com.gesthelp.vote.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuestionDto {
	Long id, scrutinId;
	String qtext;
	String numquestion;
	List<QuestionItemDto> items;
	Integer nbCheckedMin, nbCheckedMax;
	Integer nbCheckedMinExpected, nbCheckedMaxExpected;

	Boolean isAnswered = false;
}
