package com.gesthelp.vote.web.form;

import java.time.LocalDateTime;
import java.util.Date;

import com.gesthelp.vote.domain.ScrutinEtat;

import lombok.Data;

@Data
public class ScrutinInfoDto {
	Long id;
	String nature;
	LocalDateTime dateOuverture;
	LocalDateTime dateFermeture;
	ScrutinEtat phase;
	Date dateCreation;
	Integer nbQuestions;
	String hashBuffer, hash;
	Long quorum;
}
