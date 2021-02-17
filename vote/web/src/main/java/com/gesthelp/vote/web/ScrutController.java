package com.gesthelp.vote.web;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.service.JasperService;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.web.form.ScrutinInfoDto;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/scrut")
@Log
public class ScrutController extends BaseController {
	@Autowired
	private ScrutinService scrutinService;
	@Autowired
	private JasperService jasperService;
//	@Autowired
//	private UtilisateurService utilisateurService;

	@GetMapping("/")
	public String home(Model model) {
		log.info("scrut home IN  ");
		Long scrId = (Long) this.getSessionObject(SCRUT_SCRUTIN_ID_SESSION_KEY);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrId);
		ScrutinInfoDto dto = DtoUtils.dto(s);
		model.addAttribute("scrutin", dto);
		// log.info("Scrutin " + dto);
		// List<Utilisateur> scrutateurs =
		// utilisateurService.findScrutateurs(getUserId(), scrId);
		// List<UtilisateurDto> scrutateursDto = scrutateurs.stream().map(u ->
		// DtoUtils.dto(u)).collect(Collectors.toList());
		// model.addAttribute("scrutateurs", scrutateursDto);
		return "scrutation";
	}

	@GetMapping("/exportEmargReport")
	public void exportEmargReport(HttpServletResponse response) throws Exception {
		Long scrId = (Long) this.getSessionObject(SCRUT_SCRUTIN_ID_SESSION_KEY);
		Scrutin s = scrutinService.findScrutinScrut(getUserId(), scrId);
		byte[] file = jasperService.exportEmargementReport(s, true);
		response.setContentType("application/pdf");
		IOUtils.copy(new ByteArrayInputStream(file), response.getOutputStream());
	}
}
