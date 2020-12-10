package com.gesthelp.vote.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gesthelp.vote.domain.Scrutin;
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

	private void addScrutinList(Model model) {
		Page<Scrutin> page = this.scrutinService.findAll(PageRequest.of(0, 10));
		List<Scrutin> list = page.get().collect(Collectors.toList());
		List<ScrutinInfoDto> dtolist = list.stream().map(s -> DtoUtils.dto(s)).collect(Collectors.toList());
		model.addAttribute("items", dtolist);
	}

	@GetMapping("/")
	public String home(Model model) {
		log.info("AdminScrutinController home IN");
		addScrutinList(model);
		return "/admin/scrutins";
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/resultats")
	public String resultats(@RequestParam(name = "scrid", required = true) Long scrutinId, Model model) {
		log.info("resultats IN " + scrutinId);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrutinId);
		List<Utilisateur> scrutateurs = this.utilisateurService.findScrutateurs(getUserId(), scrutinId);
		if (scrutateurs == null || scrutateurs.isEmpty() || scrutateurs.size() == 1) {
			scrutateurs = this.utilisateurService.createScrutateurs(getUserId(), scrutinId, 5);
			for (Utilisateur scrut : scrutateurs) {
				scrut.setMdp(passwordEncoder.encode(scrut.getMdpClair()));
				scrut = this.utilisateurService.save(scrut);
				this.scrutinService.saveUserScrutin(scrut, s);
			}
		}
		List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u -> DtoUtils.dto(u)).collect(Collectors.toList());
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		log.info("scrutateurs " + scrutateurs);
		model.addAttribute("scrutateurs", scrutateursDto);

		return "/admin/resultats";
	}

}