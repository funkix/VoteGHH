package com.gesthelp.vote.web.form;

import java.util.List;

import lombok.Data;

@Data
public class ScrutinQuestionForm {

	Long id, scrutinId;
	// @NotEmpty(message = "{notempty}")
	// String response;
	List<String> checkResponses;
}
