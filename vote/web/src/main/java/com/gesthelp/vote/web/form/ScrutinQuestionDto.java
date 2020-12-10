package com.gesthelp.vote.web.form;

import lombok.Data;

@Data
public class ScrutinQuestionDto {
	Long id, scrutinId;
	String response;
}
