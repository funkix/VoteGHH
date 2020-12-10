package com.gesthelp.vote.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	Long numquestion;

	String questiontext;

	@ManyToOne
	@JoinColumn(name = "id_scrutin", nullable = false)
	Scrutin scrutin;

	@Column(name = "id_scrutin", insertable = false, updatable = false)
	Long scrutinId;

	// nombre de choix min attendu
	@Column(name = "nb_choix_attendus_min")
	Integer nbCheckedMinExpected;

	// nombre de choix min accepté/validé
	@Column(name = "nb_choix_min")
	Integer nbCheckedMin;

	@Column(name = "nb_choix_attendus_max")
	Integer nbCheckedMaxExpected;

	@Column(name = "nb_choix_max")
	Integer nbCheckedMax;

	@OneToMany(mappedBy = "question")
	List<QuestionItem> choices;

	@Override
	public String toString() {
		return "Question [id=" + id + ", numquestion=" + numquestion + ", questiontext=" + questiontext + ", scrutinId=" + scrutinId
				+ ", nbCheckedMin=" + nbCheckedMin + "]";
	}

}
