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
@Table(name = "question_choix")
@Data
public class QuestionItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	Question question;

	@Column(name = "question_id", insertable = false, updatable = false)
	Long questionId;

	//@Column(name = "questiontext")
	String questiontext;

}
