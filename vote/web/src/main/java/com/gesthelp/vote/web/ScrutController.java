package com.gesthelp.vote.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.web.form.ScrutinInfoDto;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/scrut")
@Log
public class ScrutController extends BaseController {
	@Autowired
	private ScrutinService scrutinService;
//	@Autowired
//	private UtilisateurService utilisateurService;

	@GetMapping("/")
	public String home(Model model) {
		Long scrId = (Long) this.getSessionObject(SCRUT_SCRUTIN_ID_SESSION_KEY);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		log.info("Scrutin  " + dto);
		// List<Utilisateur> scrutateurs =
		// utilisateurService.findScrutateurs(getUserId(), scrId);
		// List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u ->
		// DtoUtils.dto(u)).collect(Collectors.toList());
		// model.addAttribute("scrutateurs", scrutateursDto);
		return "scrutation";
	}

}
