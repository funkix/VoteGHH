package com.gesthelp.vote.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "question_choix_reponse")
@Data
public class QuestionRespItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "question_reponse_id")
	Long questionReponseId;

	@ManyToOne
	@JoinColumn(name = "question_reponse_id", nullable = false, insertable = false, updatable = false)
	QuestionResp response;

	@Column(name = "question_choix_id")
	Long questionItemId;

	@ManyToOne
	@JoinColumn(name = "question_choix_id", nullable = false, insertable = false, updatable = false)
	QuestionItem checkedItem;

	@Override
	public String toString() {
		return "QuestionRespItem [id=" + id + ", questionReponseId=" + questionReponseId + ", questionItemId=" + questionItemId + "]";
	}

}
