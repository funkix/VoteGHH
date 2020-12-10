package com.gesthelp.vote.service;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.QuestionResp;
import com.gesthelp.vote.domain.QuestionRespItem;
import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinEtat;
import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.ScrutinVoteKey;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.repository.QuestionRespItemRepository;
import com.gesthelp.vote.repository.QuestionRespRepository;
import com.gesthelp.vote.repository.ScrutinRepository;
import com.gesthelp.vote.repository.ScrutinVoteRepository;
import com.gesthelp.vote.service.dto.QuestionDto;
import com.gesthelp.vote.service.dto.QuestionItemDto;
import com.gesthelp.vote.service.exception.DejaVoteException;
import com.gesthelp.vote.service.exception.VoteRuntimeException;

import lombok.extern.java.Log;

@Service
@Log
@Transactional
public class ScrutinService {
	@Autowired
	private ScrutinRepository repository;
	@Autowired
	private ScrutinVoteRepository scrutinVoteRepository;
	@Autowired
	private QuestionRespRepository questionRespRepository;
	@Autowired
	private QuestionRespItemRepository questionRespItemRepository;

	public ScrutinVote saveUserScrutin(Utilisateur u, Scrutin s) {
		ScrutinVote sv = new ScrutinVote();
		sv.setUtilisateur(u);
		sv.setScrutin(s);
		sv.setId(new ScrutinVoteKey());
		sv.getId().setScrutinId(s.getId());
		sv.getId().setUtilisateurId(u.getId());
		return this.scrutinVoteRepository.save(sv);
	}

	public List<Scrutin> listOpenScrutins(Long userId) {
		Date today = new Date();
		List<Scrutin> res = this.repository.listUserScrutins(userId, today, ScrutinEtat.PRODUCTION.value());
		for (Scrutin scr : res) {
			ScrutinVote test = scrutinVoteRepository.findById(new ScrutinVoteKey(scr.getId(), userId)).orElse(null);
			log.info("ici " + test);
		}
		log.info("listOpenScrutins for " + userId + " returns " + res.size() + " results");
		return res;
	}

	public Scrutin findScrutinVotant(Long userId, Long scrutinId) {
		Scrutin scrutin = repository.findScrutinVotant(userId, scrutinId, new Date(), ScrutinEtat.PRODUCTION.value());
		log.info("listOpenScrutins for " + userId + " and scrutinId " + scrutinId + " returns " + scrutin);
		return scrutin;
	}

	public Scrutin findScrutinScrut(Long userId, Long scrutinId) {
		Scrutin scrutin = repository.findById(scrutinId).orElse(null);
		log.info("listOpenScrutins for " + userId + " and scrutinId " + scrutinId + " returns " + scrutin);
		return scrutin;
	}

	public ScrutinVote findScrutinVote(Long userId, Long scrutinId) {
		return scrutinVoteRepository.findById(new ScrutinVoteKey(scrutinId, userId)).orElse(null);
	}

	public boolean vote(Long userId, Long scrutinId, List<QuestionDto> questions) {
		if (isVoteComplete(userId, scrutinId, questions)) {
			return false;
		}
		ScrutinVote vote = scrutinVoteRepository.findById(new ScrutinVoteKey(scrutinId, userId)).orElse(null);
		if (vote == null) {
			throw new VoteRuntimeException("vote_not_found", "vote not found scrutinId " + scrutinId);
		}
		if (vote.getVoteDate() != null) {
			log.warning("tentative de second vote, utilisateurId=" + userId + ", scrutinId=" + scrutinId);
			throw new DejaVoteException();
		}
		// maj scrutin vote date ("a voté")
		this.repository.setScrutinVoteDate(userId, scrutinId, new Date());

		for (QuestionDto q : questions) {
			QuestionResp resp = new QuestionResp();
			resp.setUtilisateurId(userId);
			resp.setQuestionId(q.getId());
			resp.setResponseDate(new Date()); // scrutin date ?
			resp.setScrutinId(scrutinId);
			Long nbChecked = (q.getItems() == null || q.getItems().isEmpty()) ? 0 : q.getItems().stream().filter(r -> r.getIsChecked()).count();
			resp.setNbChecked(nbChecked.intValue());
			QuestionResp saved = this.questionRespRepository.save(resp);
			List<QuestionItemDto> checkedItems = q.getItems().stream().filter(item -> item.getIsChecked()).collect(Collectors.toList());
			checkedItems.forEach(item -> {
				QuestionRespItem r = new QuestionRespItem();
				r.setQuestionItemId(item.getId());
				r.setQuestionReponseId(saved.getId());
				this.questionRespItemRepository.save(r);
			});
			log.info("saving resp " + resp);
		}

		return true;
	}

	private boolean isVoteComplete(Long userId, Long scrutinId, List<QuestionDto> questions) {
		Scrutin scrutinDomain = repository.findScrutinVotant(userId, scrutinId, new Date(), ScrutinEtat.PRODUCTION.value());
		if (scrutinDomain == null) {
			log.warning("vote impossible, scrutin non trouve pour id " + scrutinId);
			return false;
		}
		if (questions == null || questions.isEmpty()) {
			log.warning("vote impossible, scrutin sans questions pour id " + scrutinId + "");
			return false;
		}
		int nbQ = scrutinDomain.getQuestions().size();
		if (questions.size() != nbQ) {
			log.warning("vote impossible, scrutin nb questions incoherent " + questions.size() + " / " + nbQ);
			return false;
		}
		boolean allMatched = true;
		for (QuestionDto q : questions) {
			boolean qIsound = scrutinDomain.getQuestions().stream().filter(qd -> qd.getId().equals(q.getId())).count() == 1;
			if (!qIsound) {
				allMatched = false;
				log.warning("vote impossible, scrutin questions match incoherent for " + q.getId());
			}
		}
		if (!allMatched) {
			return false;
		}
		return false;
	}

	public void aVote(Long utilisateurId, Long scrutinId) {
		ScrutinVote vote = this.findScrutinVote(utilisateurId, scrutinId);
		if (vote.getVoteDate() != null) {
			log.warning("tentative de second vote, utilisateurId=" + utilisateurId + ", scrutinId=" + scrutinId);
			throw new RuntimeException("scrutin déja voté");
		}
		this.repository.setScrutinVoteDate(utilisateurId, scrutinId, new Date());
	}

	public Page<Scrutin> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
