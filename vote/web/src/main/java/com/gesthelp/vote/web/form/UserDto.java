package com.gesthelp.vote.web.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserDto {

	@NotEmpty(message = "{notempty}")
	@Email(message = "{mail.format}")
	String mail;

	@NotEmpty(message = "{notempty}")
	@Size(min = 3, max = 20, message = "{mdp.size}")
	String mdp;

	@NotEmpty(message = "{notempty}")
	String nom;

	@NotEmpty(message = "{notempty}")
	String prenom;
	
	String role;

}
