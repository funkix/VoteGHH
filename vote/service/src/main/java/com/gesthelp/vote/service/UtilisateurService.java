package com.gesthelp.vote.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.repository.UtilisateurRepository;

import lombok.extern.java.Log;

@Service
@Log
@Transactional
public class UtilisateurService {
	@Autowired
	private UtilisateurRepository repo;

	@Autowired
	private ScrutinService scrutinService;

	public Utilisateur findById(Long id) {
		return this.repo.findById(id).orElse(null);
	}

	public Utilisateur findByMail(String mail) {
		return this.repo.findByMail(mail);
	}

	public Utilisateur save(Utilisateur u) {
		log.info("save IN " + u);
		// cryptage mot de passe
		Utilisateur saved = this.repo.save(u);
		return saved;
	}

	public List<Utilisateur> findScrutateurs(Long userId, Long scrutinId) {
		return repo.findByScrutinIdAndRole(scrutinId, SecurityRoles.SCRUTATEUR);
	}

	public List<Utilisateur> createScrutateurs(Long userId, Long scrutinId, int nbScrutateurs) {
		List<Utilisateur> res = new ArrayList<Utilisateur>();
		Scrutin scrutin = scrutinService.findScrutinVotant(userId, scrutinId);
		for (int i = 0; i < nbScrutateurs; i++) {
			Utilisateur u = new Utilisateur();
			String userMail = "scrut" + scrutinId + "" + (i + 7) + "@votesecure.fr";
			userMail = userMail.replaceAll("\u0000", "");
			u.setMail(userMail);
			u.setMdpClair(PasswordGenerator.generateEasyPassword(4).replaceAll("\u0000", ""));
			u.setRole(SecurityRoles.SCRUTATEUR);
			res.add(u);
		}
		log.info("createScrutateurs OUT " + userId + ", scrId=" + scrutinId + ", nb=" + nbScrutateurs + ", res=" + res);
		return res;

	}

}
