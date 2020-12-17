package com.gesthelp.vote.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Utilisateur> findOrCreateScrutateurs(Long userId, Long scrutinId) {
		List<Utilisateur> list = repo.findByScrutinIdAndRole(scrutinId, SecurityRoles.ROLE_SCRUTATEUR);
		if (list == null || list.isEmpty()) {
			list = this.createScrutateurs(userId, scrutinId, 5);
		}
		return list;
	}

	public List<Utilisateur> findScrutateurs(Long userId, Long scrutinId) {
		return repo.findByScrutinIdAndRole(scrutinId, SecurityRoles.ROLE_SCRUTATEUR);
	}

	private List<Utilisateur> createScrutateurs(Long userId, Long scrutinId, int nbScrutateurs) {
		List<Utilisateur> res = generateUtilisateurs(userId, scrutinId, 5, "scrut", SecurityRoles.ROLE_SCRUTATEUR);
		log.info("createScrutateurs OUT " + userId + ", scrId=" + scrutinId + ", nb=" + nbScrutateurs + ", res=" + res);
		return res;
	}

	public List<Utilisateur> findOrCreateVotantsRecette(Long userId, Long scrutinId) {
		List<Utilisateur> list = repo.findByScrutinIdAndRole(scrutinId, SecurityRoles.ROLE_VOTANT_RECETTE);
		if (list == null || list.isEmpty()) {
			list = this.createVotantsRecette(userId, scrutinId, 5);
		}
		return list;
	}

	private List<Utilisateur> createVotantsRecette(Long userId, Long scrutinId, int nbScrutateurs) {
		List<Utilisateur> res = generateUtilisateurs(userId, scrutinId, 5, "rec", SecurityRoles.ROLE_VOTANT_RECETTE);
		log.info("createVotantsRecette OUT " + userId + ", scrId=" + scrutinId + ", nb=" + nbScrutateurs + ", res=" + res);
		return res;
	}

	private List<Utilisateur> generateUtilisateurs(Long userId, Long scrutinId, int nbScrutateurs, String mailprefix, String role) {
		List<Utilisateur> res = new ArrayList<Utilisateur>();
		for (int i = 0; i < nbScrutateurs; i++) {
			// creation utilisateur
			Utilisateur utilisateur = new Utilisateur();
			String mail = mailprefix + scrutinId + "" + (i + 7) + "@votesecure.fr";
			mail = mail.replaceAll("\u0000", "");
			utilisateur.setMail(mail);
			utilisateur.setMdpClair(PasswordGenerator.generateEasyPassword(4).replaceAll("\u0000", ""));
			utilisateur.setMdp(passwordEncoder.encode(utilisateur.getMdpClair()));
			utilisateur.setRole(role);
			// save utilisateur
			utilisateur = this.save(utilisateur);
			res.add(utilisateur);
			// save utilisateur_scrutin
			this.scrutinService.saveUserScrutin(utilisateur, scrutinId);
		}
		log.info("createScrutateurs OUT " + userId + ", scrId=" + scrutinId + ", nb=" + nbScrutateurs + ", res=" + res);
		return res;
	}
}
