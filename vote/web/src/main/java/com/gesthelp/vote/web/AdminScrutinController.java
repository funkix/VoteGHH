package com.gesthelp.vote.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinEtat;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.service.UtilisateurService;
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

	@GetMapping("/")
	public String home(Model model) {
		log.info("AdminScrutinController home IN");
		List<Scrutin> list = this.scrutinService.listAdminScrutins(getUserId());
		List<ScrutinInfoDto> dtolist = list.stream().map(s -> DtoUtils.dto(s)).collect(Collectors.toList());
		model.addAttribute("items", dtolist);
		return "/admin/scrutins";
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/resultats")
	public String resultats(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateScrutateurs(getUserId(), scrutinId);
		List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		log.info("scrutateurs " + scrutateurs);
		model.addAttribute("scrutateurs", scrutateursDto);

		return "/admin/resultats";
	}

	@GetMapping("/scrutin")
	public String scrutin(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		if (ScrutinEtat.PRODUCTION.equals(s.getPhase())) {
			// scrutateurs
			List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateScrutateurs(getUserId(), scrutinId);
			List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
			model.addAttribute("scrutateurs", scrutateursDto);
		} else if (ScrutinEtat.RECETTE.equals(s.getPhase())) {
			List<Utilisateur> scrutateurs = this.utilisateurService.findOrCreateVotantsRecette(getUserId(), scrutinId);
			List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
			model.addAttribute("votantsRecette", scrutateursDto);
		}
		model.addAttribute("scrutin", dto);
		return "/admin/scrutin";
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
		return "/admin/scrutin-hash";
	}

	@GetMapping("/scrutin-hash-save")
	public String saveScrutinHash(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model, RedirectAttributes ratt) {
		log.info("saveScrutinHash IN " + scrutinId);
		Scrutin scr = this.scrutinService.findScrutinById(scrutinId);
		String hash = this.scrutinService.saveScrutinHash(scr);
		log.info("saveScrutinHash done " + hash);
		ratt.addAttribute("scrid", scrutinId);
		ratt.addAttribute("sd", "o");
		return "redirect:/admin/scrutins/scrutin-hash";
	}

}
