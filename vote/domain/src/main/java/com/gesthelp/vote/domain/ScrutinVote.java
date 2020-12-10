package com.gesthelp.vote.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "utilisateur_scrutin")
@Data
public class ScrutinVote {

	@EmbeddedId
	ScrutinVoteKey id;

	@ManyToOne
	@MapsId("utilisateurId")
	@JoinColumn(name = "utilisateur_id", nullable = false, insertable = false, updatable = false)
	Utilisateur utilisateur;

	@ManyToOne
	@MapsId("scrutinId")
	@JoinColumn(name = "scrutin_id", nullable = false, insertable = false, updatable = false)
	Scrutin scrutin;

	@Column(name = "date_vote")
	Date voteDate;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScrutinVote other = (ScrutinVote) obj;
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

	@Override
	public String toString() {
		return "ScrutinVote [id=" + id + ", voteDate=" + voteDate + "]";
	}

}
