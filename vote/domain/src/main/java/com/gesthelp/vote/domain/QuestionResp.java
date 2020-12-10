package com.gesthelp.vote.domain;


import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "question_reponse")
@Data
public class QuestionResp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@NotNull 
	Long utilisateurId;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false, insertable = false, updatable = false)
	Question question;

	@Column(name = "question_id")
	Long questionId;

	@Column(name = "date_reponse")
	Date responseDate;

	Integer nbChecked;

	// @OneToMany(mappedBy = "response")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	List<QuestionRespItem> checked;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false, insertable = false, updatable = false)
	Scrutin scrutin;

	@Column(name = "scrutin_id")
	Long scrutinId;

}
