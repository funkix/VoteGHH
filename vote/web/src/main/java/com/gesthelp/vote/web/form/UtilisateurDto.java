package com.gesthelp.vote.web.form;

import lombok.Data;

@Data
public class UtilisateurDto {

	private Long id;
	private String mdpClair;
	private String role;
	private String nom;
	private String prenom;
	private String mail;
	private Long extraId;

}
