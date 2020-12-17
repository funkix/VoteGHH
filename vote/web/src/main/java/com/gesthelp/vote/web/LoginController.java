package com.gesthelp.vote.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.SecurityRoles;
import com.gesthelp.vote.service.UtilisateurService;

import lombok.extern.java.Log;

@Controller
@Log
public class LoginController extends BaseController {

	@Autowired
	private UtilisateurService utilisateurService;

	@GetMapping("/login")
	public String loginPage(Model model) {
		return "login";
	}

	@GetMapping("/loginSuccess")
	public String loginSuccess(Authentication authentication) {
		log.info("loginSuccess IN " + authentication);
		Utilisateur utilisateur = this.utilisateurService.findByMail(authentication.getName());
		// mise en session de l'id utilisateur
		super.setUserId(utilisateur.getId());
		log.info("loginSuccess IN userId =" + utilisateur.getId());
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(SecurityRoles.ROLE_SCRUTATEUR))) {
			log.info("loginSuccess SCRUTATEUR case ");
			Set<ScrutinVote> votes = utilisateur.getScrutinVote();
			if (votes.isEmpty()) {
				throw new UsernameNotFoundException("SCRUTATEUR sans scrutin " + utilisateur.getId());
			}
			if (votes.size() > 1) {
				throw new UsernameNotFoundException("SCRUTATEUR multi scrutin " + utilisateur.getId());
			}
			Scrutin scrutin = votes.iterator().next().getScrutin();
			setSessionObject(SCRUT_SCRUTIN_ID_SESSION_KEY, scrutin.getId());
			return "redirect:/scrut/";
		} else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(SecurityRoles.ROLE_VOTANT_RECETTE))) {
			log.info("loginSuccess VOTANT_RECETTE case ");
			Set<ScrutinVote> votes = utilisateur.getScrutinVote();
			if (votes.isEmpty()) {
				throw new UsernameNotFoundException("VOTANT_RECETTE sans scrutin " + utilisateur.getId());
			}
			if (votes.size() > 1) {
				throw new UsernameNotFoundException("VOTANT_RECETTE multi scrutin " + utilisateur.getId());
			}
			Scrutin scrutin = votes.iterator().next().getScrutin();
			setSessionObject(SCRUT_SCRUTIN_ID_SESSION_KEY, scrutin.getId());
			return "redirect:/vote/scrutins/";
		}

		else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(SecurityRoles.ROLE_ADMIN))) {
			log.info("loginSuccess Admin case ");
			return "redirect:/admin/scrutins/";
		} else {
			log.info("loginSuccess Votant case ");
			return "redirect:/vote/scrutins/";
		}
	}

}
