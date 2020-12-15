package com.gesthelp.vote.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.service.dto.QuestionDto;
import com.gesthelp.vote.service.dto.QuestionItemDto;
import com.gesthelp.vote.service.exception.VoteRuntimeException;
import com.gesthelp.vote.web.form.ScrutinQuestionForm;
import com.gesthelp.vote.web.form.ScrutinQuestionsDto;
import com.gesthelp.vote.web.form.ScrutinSessionDto;
import com.gesthelp.vote.web.form.ScrutinVotantDto;

import lombok.extern.java.Log;

@Controller
@RequestMapping("vote")
@Transactional
@Log
public class VotantController extends BaseController {
	@Autowired
	private ScrutinService scrutinService;

	@GetMapping("/scrutins")
	public String scrutins(Authentication authentication, Model model) {
		log.info("scrutins IN " + getUserId());
		// liste des scrutins ouverts pour l'utilisateur
		List<Scrutin> scrutins = scrutinService.listOpenScrutins(getUserId());
		// liste des donnees de votes de l'utilisateur
		List<ScrutinVotantDto> scrutinsVotant = new ArrayList<ScrutinVotantDto>();
		for (Scrutin s : scrutins) {
			ScrutinVote vote = this.scrutinService.findScrutinVote(getUserId(), s.getId());
			scrutinsVotant.add(DtoUtils.dto(s, vote));
		}
		model.addAttribute("items", scrutinsVotant);
		return "scrutins";
	}

	/**
	 * accès à une question du vote en cours (session)
	 */
	@GetMapping("voteStep")
	public String voteStep(@RequestParam(name = "scrid", required = false) Long scrutinId, @RequestParam(name = "q", required = false) String resume,
			Model model) {
		log.info("voteStep IN scrid=" + scrutinId + ", q=" + resume);
		// scrutin
		ScrutinSessionDto sessionScrDto = getSessionScrutin(scrutinId, resume);
		ScrutinQuestionForm fo = dto(sessionScrDto.getId(), sessionScrDto.getQuestion().getId());
		// reponse
		QuestionDto resp = sessionScrDto.getQuestions().stream().filter(s -> s.getId().equals(sessionScrDto.getQuestion().getId())).findFirst()
				.orElse(null);
		if (resp != null && resp.getItems() != null && !resp.getItems().isEmpty()) {
			fo.setCheckResponses(
					resp.getItems().stream().filter(r -> r.getIsChecked()).map(r -> "" + r.getQuestionId()).collect(Collectors.toList()));
		}
		model.addAttribute("item", sessionScrDto);
		model.addAttribute("scrutinQuestionForm", fo);
		return "scrutin-vote-question";
	}

	/**
	 * reponse à une question du vote en cours (session)
	 */
	@PostMapping("voteStep")
	public String postVoteStep(@Valid ScrutinQuestionForm form, BindingResult bindingResult, Model model) {
		log.info("postVoteStep IN " + form);
		// scrutin
		ScrutinSessionDto scrDto = getSessionScrutin(null, null);
		// on retrouve la question encours
		QuestionDto question = scrDto.getQuestions().stream().filter(s -> s.getId().equals(form.getId())).findFirst().orElse(null);
		if (question == null) {
			throw new VoteRuntimeException("question_not_found", "question_not_found " + form.getId());
		}
		// on met a jour les "item checked"
		question.getItems()
				.forEach(item -> item.setIsChecked(form.getCheckResponses() != null && form.getCheckResponses().contains("" + item.getId())));
		this.validate(form, bindingResult, question);
		if (bindingResult.hasErrors()) {
			model.addAttribute("scrutinQuestionForm", form);
			model.addAttribute("item", scrDto);
			return "scrutin-vote-question";
		}
		// maj question isAnswered
		question.setIsAnswered(true);
		// maj flag "toutes questions votées"
		if (!scrDto.getAllQuestionsAnswered()) {
			long nbRemaingQuestions = scrDto.getQuestions().stream().filter(q -> !q.getIsAnswered()).count();
			if (nbRemaingQuestions == 0) {
				scrDto.setAllQuestionsAnswered(true);
			}
		}
		// direction la question d'index suivant ou resumé
		Integer nextQuestionIdx = scrDto.getQuestionIndex() + 1;
		if (scrDto.getAllQuestionsAnswered() || nextQuestionIdx.compareTo(scrDto.getQuestions().size()) >= 0) {
			// cas de la dernière étape ou retour sur choix, on affiche le resume
			model.addAttribute("item", scrDto);
			return "redirect:/vote/resume";
		}
		return "redirect:/vote/voteStep?q=" + nextQuestionIdx;
	}

	private void validate(ScrutinQuestionForm form, BindingResult bindingResult, QuestionDto resp) {
		int nbCheckedResp = (form.getCheckResponses() == null || form.getCheckResponses().isEmpty()) ? 0 : form.getCheckResponses().size();
		if (resp.getNbCheckedMin().intValue() > 0) {
			if (resp.getNbCheckedMin() == 1 && nbCheckedResp == 0) {
				bindingResult.rejectValue("checkResponses", "notempty");
			} else if (nbCheckedResp < resp.getNbCheckedMin().intValue()) {
				bindingResult.rejectValue("checkResponses", "choices.minNotReached");
			}
		}
		if (resp.getNbCheckedMax().intValue() > 0) {
			if (nbCheckedResp > resp.getNbCheckedMax()) {
				bindingResult.rejectValue("checkResponses", "choices.maxExceeded");
			}
		}

	}

	@GetMapping("resume")
	public String resumeStep(Model model) {
		ScrutinSessionDto scrDto = getSessionScrutin(null, null);
		model.addAttribute("item", scrDto);
		return "scrutin-vote-resume";
	}

	@GetMapping("voteConfirm")
	public String voteConfirmStep(Model model) {
		ScrutinSessionDto scrDto = getSessionScrutin(null, null);
		// this.scrutinService.aVote(getUserId(), scrDto.getId());
		ScrutinVote sv = this.scrutinService.vote(getUserId(), scrDto.getId(), scrDto.getQuestions());
		log.info("voteConfirmStep ScrutinVote " + sv);
		model.addAttribute("voteDate", sv.getVoteDate());
		model.addAttribute("voteHash", sv.getVoteHash());
		// super.setSessionObject(SCRUTIN_SESSION_KEY, null);
		return "scrutin-vote-success";
	}

	private ScrutinQuestionForm dto(Long scrId, Long qId) {
		ScrutinQuestionForm qfo = new ScrutinQuestionForm();
		qfo.setId(qId);
		qfo.setScrutinId(scrId);
		return qfo;
	}

	private ScrutinQuestionsDto dto(Scrutin scrutin) {
		ScrutinQuestionsDto fo = new ScrutinQuestionsDto();
		fo.setScrutinId(scrutin.getId());
		fo.setScrutinText(scrutin.getNature());
		fo.setQuestions(scrutin.getQuestions().stream().map(q -> DtoUtils.dto(q)).collect(Collectors.toList()));
		return fo;
	}

	private ScrutinSessionDto getSessionScrutin(Long scrutinId, String stepNb) {
		ScrutinSessionDto sessionScrDto = (ScrutinSessionDto) super.getSessionObject(SCRUTIN_SESSION_KEY);
		if (sessionScrDto == null) {
			// première arrivée à la question 1
			if (scrutinId == null) {
				// on doit obligatoirement avoir l'id du scrutin
				throw new VoteRuntimeException("scrutinId_not_found", "scrutinId obligatoire  ");
			}
			Scrutin scr = scrutinService.findScrutinVotant(getUserId(), scrutinId);
			ScrutinVote vote = this.scrutinService.findScrutinVote(getUserId(), scrutinId);
			sessionScrDto = DtoUtils.sessionDto(scr, vote);
			super.setSessionObject(SCRUTIN_SESSION_KEY, sessionScrDto);
		} else {
			if (scrutinId != null && !sessionScrDto.getId().equals(scrutinId)) {
				throw new VoteRuntimeException("scrutinId_mismatch", "scrutinId mismatch " + scrutinId);
			}
		}
		// question
		Integer q = sessionScrDto.getQuestionIndex();
		if (StringUtils.isNotBlank(stepNb) && StringUtils.isNumeric(stepNb)) {
			q = new Integer(stepNb);
		}
		if (q.compareTo(sessionScrDto.getQuestions().size()) >= 0) {
			throw new VoteRuntimeException("index_error", "index_error for" + q);
		}
		QuestionDto encours = sessionScrDto.getQuestions().get(q);
		sessionScrDto.setQuestion(encours);
		sessionScrDto.setQuestionIndex(q);

		return sessionScrDto;
	}

	/**
	 * Affichage de toutes les question d'un scrutin
	 */
	@PostMapping("/scrutin")
	public String scrutin(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		Scrutin scrutin = scrutinService.findScrutinVotant(getUserId(), scrutinId);
		model.addAttribute("item", scrutin);
		ScrutinQuestionsDto fo = dto(scrutin);
		// attention le nom du form doit etre le nom de la classe en CamelCase sinon
		// thymeleaf utility #errors ne fonctionne pas (a besoin de ce nom par défaut)
		model.addAttribute("scrutinQuestionsDto", fo);
		log.info("valueBefore: " + fo);
		return "scrutin-vote";
	}

	/**
	 * POST du vote de toutes les questions du scrutin
	 */
	@PostMapping("/dovote")
	public String dovote(@ModelAttribute ScrutinQuestionsDto form, BindingResult bindingResult, Model model) {
		log.info("value: " + form);
		if (form.getQuestions() == null || form.getQuestions().isEmpty()) {
			bindingResult.rejectValue("test", "{notempty}");
		} else {
			// on vérifie les réponses de chaque question
			for (int i = 0; i < form.getQuestions().size(); i++) {
				List<QuestionItemDto> list = form.getQuestions().get(i).getItems();
				if (list == null || list.isEmpty()) {
					bindingResult.rejectValue("questions[" + i + "].response", "{notempty}", "Obligatoire");
				}
			}
		}
		if (bindingResult.hasErrors()) {
			Scrutin scrutin = scrutinService.findScrutinVotant(getUserId(), form.getScrutinId());
			model.addAttribute("item", scrutin);
			// attention le nom du form doit etre le nom de la classe en CamelCase sinon
			// thymeleaf utility #errors ne fonctionne pas (a besoin de ce nom par défaut)
			ScrutinQuestionsDto dto = dto(scrutin);
			for (QuestionDto q : form.getQuestions()) {
				List<QuestionDto> question = dto.getQuestions().stream().filter(q2 -> q2.getId().equals(q.getId())).collect(Collectors.toList());
				if (!question.isEmpty()) {
					q.setQtext(question.get(0).getQtext());
				}
			}

			model.addAttribute("scrutinQuestionsDto", form);
			return "scrutin-vote";
		}
		// boolean test = scrutinService.vote(getUserId(), form);
		// log.info("vote ?: " + test);
		return "redirect:/vote/scrutins";
	}

}
