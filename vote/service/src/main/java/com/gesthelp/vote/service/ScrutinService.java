package com.gesthelp.vote.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Question;
import com.gesthelp.vote.domain.QuestionItem;
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
import com.gesthelp.vote.repository.UtilisateurRepository;
import com.gesthelp.vote.service.dto.QuestionDto;
import com.gesthelp.vote.service.dto.QuestionItemDto;
import com.gesthelp.vote.service.exception.DejaVoteException;
import com.gesthelp.vote.service.exception.VoteRuntimeException;

import lombok.extern.java.Log;

@Service
@Log
public class ScrutinService {
	@Autowired
	private ScrutinRepository repository;
	@Autowired
	private ScrutinVoteRepository scrutinVoteRepository;
	@Autowired
	private QuestionRespRepository questionRespRepository;
	@Autowired
	private QuestionRespItemRepository questionRespItemRepository;
	@Autowired
	private UtilisateurRepository utilisateurRepository;
	@Autowired
	private UtilisateurEmailService emailService;

	public Scrutin findScrutinById(Long scrutinId) {
		return repository.findById(scrutinId).orElse(null);
	}

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

	public ScrutinVote vote(Long userId, Long scrutinId, List<QuestionDto> questions) {
		if (isVoteComplete(userId, scrutinId, questions)) {
			return null;
		}
		Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
		// TODO vérifier role votant
		ScrutinVote vote = scrutinVoteRepository.findById(new ScrutinVoteKey(scrutinId, userId)).orElse(null);
		if (vote == null) {
			throw new VoteRuntimeException("vote_not_found", "vote not found scrutinId " + scrutinId);
		}
		if (vote.getVoteDate() != null) {
			log.warning("tentative de second vote, utilisateurId=" + userId + ", scrutinId=" + scrutinId);
			throw new DejaVoteException();
		}
		// maj scrutin vote date ("a voté")
		StringBuffer responseHashBuffer = new StringBuffer();
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
			responseHashBuffer.append(q.getQtext()).append("\n");
			checkedItems.forEach(item -> {
				QuestionRespItem r = new QuestionRespItem();
				r.setQuestionItemId(item.getId());
				r.setQuestionReponseId(saved.getId());
				this.questionRespItemRepository.save(r);
				responseHashBuffer.append(item.getQuestiontext()).append("\n");
			});
			log.info("saving resp " + resp);
		}
		log.info("saving vote responseHashBuffer :\n" + responseHashBuffer);
		this.repository.setScrutinVoteDate(userId, scrutinId, new Date(), this.hashBuffer(responseHashBuffer.toString()));
		vote = this.scrutinVoteRepository.findById(new ScrutinVoteKey(scrutinId, userId)).orElse(null);
		try {
			emailService.sendMessageAVote(utilisateur, vote.getVoteHash());
		} catch (Exception e) {
			log.log(Level.SEVERE, "mail non envoyé pour " + utilisateur.getMail());
		}
		return vote;
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

	public Page<Scrutin> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public String scrutinHashBuffer(Scrutin s) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(s.getNature()).append("\n");
		for (Question q : s.getQuestions()) {
			buffer.append(q.getQuestiontext().toLowerCase().trim()).append("\n");
			for (QuestionItem option : q.getChoices()) {
				buffer.append(option.getQuestiontext().toLowerCase().trim()).append("\n");
			}
		}
		return buffer.toString();
	}

	public String hashBuffer(String buffer) {
		return DigestUtils.sha256Hex(buffer);
	}

	public String saveScrutinHash(Scrutin scr) {
		if (!scr.getPhase().equals(ScrutinEtat.RECETTE)) {
			throw new RuntimeException("opération interdite pour le scrutin " + scr.getId());
		}
		String scrutinHashBuffer = this.scrutinHashBuffer(scr);
		log.info("saveScrutinHash buffer=\n" + scrutinHashBuffer);
		String sha256hex = this.hashBuffer(scrutinHashBuffer);
		log.info("saveScrutinHash sha256hex=" + sha256hex);
		scr.setHashBuffer(scrutinHashBuffer);
		scr.setHash(sha256hex);
		this.repository.save(scr);
		return scrutinHashBuffer;

	}
}
