package com.gesthelp.vote.web;

import java.util.ArrayList;
import java.util.List;

import com.gesthelp.vote.domain.Question;
import com.gesthelp.vote.domain.QuestionItem;
import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.dto.QuestionDto;
import com.gesthelp.vote.service.dto.QuestionItemDto;
import com.gesthelp.vote.web.form.ScrutinInfoDto;
import com.gesthelp.vote.web.form.ScrutinSessionDto;
import com.gesthelp.vote.web.form.ScrutinVotantDto;
import com.gesthelp.vote.web.form.UtilisateurDto;

public class DtoUtils {

	public static UtilisateurDto dto(Utilisateur u) {
		UtilisateurDto dto = new UtilisateurDto();
		dto.setId(u.getId());
		dto.setMail(u.getMail());
		dto.setMdpClair(u.getMdpClair());
		dto.setNom(u.getNom());
		dto.setPrenom(u.getPrenom());
		dto.setRole(u.getRole());
		return dto;
	}

	public static ScrutinInfoDto dto(Scrutin s) {
		ScrutinInfoDto dto = new ScrutinInfoDto();
		dto.setId(s.getId());
		dto.setDateCreation(s.getDateCreation());
		dto.setDateFermeture(s.getDateFermeture());
		dto.setDateOuverture(s.getDateOuverture());
		dto.setNature(s.getNature());
		// TODO :
		dto.setNbQuestions(s.getQuestions().size());
		dto.setPhase(s.getPhase());
		dto.setHash(s.getHash());
		dto.setHashBuffer(s.getHashBuffer());
		return dto;
	}

	public static ScrutinSessionDto sessionDto(Scrutin s, ScrutinVote v) {
		ScrutinSessionDto dto = new ScrutinSessionDto();
		dto.setQuestionIndex(0);
		dto.setId(s.getId());
		dto.setNature(s.getNature());
		dto.setPhase(s.getPhase());
		List<QuestionDto> questions = new ArrayList<QuestionDto>();
		if (s.getQuestions() != null) {
			for (Question q : s.getQuestions()) {
				questions.add(dto(q));
			}
			dto.setNbQuestions(questions.size());
		}
		dto.setQuestions(questions);
		dto.setDateCreation(s.getDateCreation());
		dto.setDateFermeture(s.getDateFermeture());
		dto.setDateOuverture(s.getDateOuverture());
		dto.setDateVote(v.getVoteDate());
		return dto;

	}

	public static ScrutinVotantDto dto(Scrutin s, ScrutinVote v) {
		ScrutinVotantDto dto = new ScrutinVotantDto();
		dto.setId(s.getId());
		dto.setNature(s.getNature());
		dto.setPhase(s.getPhase());
		List<QuestionDto> questions = new ArrayList<QuestionDto>();
		if (s.getQuestions() != null) {
			for (Question q : s.getQuestions()) {
				questions.add(dto(q));
			}
			dto.setNbQuestions(questions.size());
		}
		dto.setQuestions(questions);
		dto.setDateCreation(s.getDateCreation());
		dto.setDateFermeture(s.getDateFermeture());
		dto.setDateOuverture(s.getDateOuverture());
		dto.setDateVote(v.getVoteDate());
		return dto;
	}

	public static QuestionDto dto(Question q) {
		QuestionDto qfo = new QuestionDto();
		qfo.setId(q.getId());
		qfo.setQtext(q.getQuestiontext());
		qfo.setScrutinId(q.getScrutinId());
		qfo.setItems(new ArrayList<>());
		for (QuestionItem item : q.getChoices()) {
			qfo.getItems().add(dto(item));
		}
		qfo.setNbCheckedMax(q.getNbCheckedMax());
		qfo.setNbCheckedMaxExpected(q.getNbCheckedMaxExpected());
		qfo.setNbCheckedMin(q.getNbCheckedMin());
		qfo.setNbCheckedMinExpected(q.getNbCheckedMinExpected());
		return qfo;
	}

	public static QuestionItemDto dto(QuestionItem item) {
		QuestionItemDto dto = new QuestionItemDto();
		dto.setId(item.getId());
		dto.setQuestiontext(item.getQuestiontext());
		dto.setQuestionId(item.getQuestionId());
		return dto;
	}

}
