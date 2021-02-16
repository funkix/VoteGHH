package com.gesthelp.vote.web.form;

import lombok.Data;

@Data
public class ScrutinDateForm {
	Long scrid;
	String dateDebut, heureDebut, dateFin, heureFin;
}
