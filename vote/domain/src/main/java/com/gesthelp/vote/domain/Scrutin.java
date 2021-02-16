package com.gesthelp.vote.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Data;

@Entity
@Data
public class Scrutin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String nature;
	@Column(name = "date_heure_debut")
	LocalDateTime dateOuverture;
	@Column(name = "date_heure_fin")
	LocalDateTime dateFermeture;
	@Enumerated(EnumType.ORDINAL)
	ScrutinEtat phase;
	@Column(name = "date_heure_creation")
	Date dateCreation;

	@Column(name = "hash")
	String hash;

	@Column(name = "hash_buffer")
	String hashBuffer;

	Long quorum;
	
//	@ManyToMany(mappedBy = "scrutins", fetch = FetchType.LAZY)
//	private Set<Utilisateur> votants;

	@OneToMany(mappedBy = "scrutin")
	Set<ScrutinVote> scrutinVote;

	@OneToMany(mappedBy = "scrutin")
	@OrderBy("numquestion")
	List<Question> questions;

	@Override
	public String toString() {
		return "Scrutin [id=" + id + ", nature=" + nature + ", dateOuverture=" + dateOuverture + ", dateFermeture=" + dateFermeture + ", phase="
				+ phase + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scrutin other = (Scrutin) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
