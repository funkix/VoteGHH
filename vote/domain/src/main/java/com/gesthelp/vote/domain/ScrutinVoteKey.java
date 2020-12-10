package com.gesthelp.vote.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScrutinVoteKey implements Serializable {
	@Column(name = "scrutin_id")
	Long scrutinId;

	@Column(name = "utilisateur_id")
	Long utilisateurId;
}
