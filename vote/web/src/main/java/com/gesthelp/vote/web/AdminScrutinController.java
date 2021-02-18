package com.gesthelp.vote.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinEtat;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.service.UserAuthentication;
import com.gesthelp.vote.service.UserCacheService;
import com.gesthelp.vote.service.UtilisateurService;
import com.gesthelp.vote.service.exception.ScrutinNotClosedException;
import com.gesthelp.vote.web.form.ScrutateurWatchedDto;
import com.gesthelp.vote.web.form.ScrutinDateForm;
import com.gesthelp.vote.web.form.ScrutinInfoDto;
import com.gesthelp.vote.web.form.UtilisateurDto;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/admin/scrutins")
@Log
public class AdminScrutinController extends BaseController {
	@Autowired
	private ScrutinService scrutinService;
	@Autowired
	private UtilisateurService utilisateurService;
	@Autowired
	private UserCacheService userCacheService;

	@GetMapping("/")
	public String home(Model model) {
		log.info("AdminScrutinController home IN");
		List<Scrutin> list = this.scrutinService.listAdminScrutins(getUserId());
		List<ScrutinInfoDto> dtolist = list.stream().map(s -> DtoUtils.dto(s)).collect(Collectors.toList());
		model.addAttribute("items", dtolist);
		return "admin/scrutins";
	}

	@GetMapping("/resultats")
	public String resultats(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateScrutateurs(getUserId(), s);
		List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		log.info("scrutateurs " + scrutateurs);
		model.addAttribute("scrutateurs", scrutateursDto);

		return "admin/resultats";
	}

	@Autowired
	private ScrutinService serviceScrutinVote;

	@GetMapping("/participation")
	public String participation(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		// Connaître le nombre de d'inscrits au scrutin
		BigDecimal nbI = serviceScrutinVote.nbInscrits(scrutinId);
		model.addAttribute("nbI", nbI);
		// Connaître le nombre de personnes ayant déjà voté au scrutin
		BigDecimal nbP = serviceScrutinVote.nbParticipants(scrutinId);
		model.addAttribute("nbP", nbP);
		// Calculer le pourcentage de participation
		BigDecimal pourcentage = serviceScrutinVote.poucentageParticipationScrutin(nbP, nbI);
		model.addAttribute("pourcentage", pourcentage);
		return "admin/participation";
	}

	@GetMapping("/scrutin")
	public String scrutin(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		if (ScrutinEtat.PRODUCTION.equals(s.getPhase())) {
			// scrutateurs
			List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateScrutateurs(getUserId(), s);
			List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
			model.addAttribute("scrutateurs", scrutateursDto);
		} else if (ScrutinEtat.RECETTE.equals(s.getPhase())) {
			List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateVotantsRecette(getUserId(), scrutinId);
			List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
			model.addAttribute("votantsRecette", scrutateursDto);
		}
		model.addAttribute("scrutin", dto);
		model.addAttribute("dateForm", new ScrutinDateForm());

		return "admin/scrutin";
	}

	@GetMapping("/scrutin-hash")
	public String scrutinhashView(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		// hashBuffer calculé
		String scrutinHashBuffer = scrutinService.scrutinHashBuffer(s);
		model.addAttribute("scrutinHashBuffer", scrutinHashBuffer);
		// hash calculé
		String scrutinHash = scrutinService.hashBuffer(scrutinHashBuffer);
		model.addAttribute("scrutinHash", scrutinHash);
		// mismatch warning
		model.addAttribute("hashMismatch", !scrutinHash.equals(s.getHash()));
		model.addAttribute("hashBufferMismatch", !scrutinHashBuffer.equals(s.getHashBuffer()));
		return "admin/scrutin-hash";
	}

	@GetMapping("/scrutin-hash-save")
	public String saveScrutinHash(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model, RedirectAttributes ratt) {
		log.info("saveScrutinHash IN " + scrutinId);
		Scrutin scr = this.scrutinService.findScrutinById(scrutinId);
		scr = this.scrutinService.buildAndSaveHash(scr);
		String hash = scr.getHash();
		log.info("saveScrutinHash done " + hash);
		ratt.addAttribute("scrid", scrutinId);
		ratt.addAttribute("sd", "o");
		return "redirect:/admin/scrutins/scrutin-hash";
	}

	@PostMapping("/production")
	public String gotoProduction(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model, RedirectAttributes ratt) {
		log.info("gotoProduction IN " + scrutinId);
		Scrutin s = this.scrutinService.toProduction(scrutinId);
		log.info("gotoProduction done " + s);
		addFlashMessage("Scrutin opérationnel", UIMessageType.INFO, true);
		ratt.addAttribute("scrid", scrutinId);
		return "redirect:/admin/scrutins/scrutin";
	}

	@PostMapping("/modifierDate")
	public String modifierDate(@ModelAttribute ScrutinDateForm dateForm, BindingResult result, RedirectAttributes ratt) {
		log.info("modifierDate IN " + dateForm);
		this.scrutinService.modifierDateHeureScrutin(dateForm.getScrid(), dateForm.getDateDebut() + " " + dateForm.getHeureDebut(),
				dateForm.getDateFin() + " " + dateForm.getHeureFin());
		log.info("modifierDate done " + dateForm);
		addFlashMessage("Date scrutin modifié", UIMessageType.INFO, true);
		ratt.addAttribute("scrid", dateForm.getScrid());
		return "redirect:/admin/scrutins/scrutin";
	}

	@GetMapping("/scrutateursWatch")
	public String scrutateursWatch(@RequestParam(name = "scrid", required = true) Long scrId, Model model) {
		log.info("scrutateursWatch IN " + scrId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrId);
		if (!scrutinService.isScrutinClosed(s)) {
			throw new ScrutinNotClosedException();
		}
		// liste des scrutateurs logués:
		List<UserAuthentication> scrutateurs = userCacheService.listUsers(scrId);
		List<ScrutateurWatchedDto> dtos = scrutateurs.stream().map(ua -> {
			ScrutateurWatchedDto dto = new ScrutateurWatchedDto();
			dto.setUserMail(ua.getAuthentication().getName());
			dto.setLogTime(ua.getLogTime());
			return dto;
		}).collect(Collectors.toList());
		model.addAttribute("items", dtos);

		// check si nombre de scrutateurs logués suffisant :
		if (dtos.size() >= s.getNbScrutateurs().intValue()) {
			model.addAttribute("nbScrutsChecked", true);
		}
		return "admin/scrutateurs-watch";
	}

}
