package com.gesthelp.vote.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.UtilisateurService;

import lombok.extern.java.Log;

@Service
@Log
@Transactional
public class UtilisateurLoginService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UtilisateurService utilisateurService;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername IN " + username);
		Utilisateur ut = utilisateurService.findByMail(username);
		log.info("loadUserByUsername OUT " + username + " => " + ut);
		if (ut == null) {
			throw new UsernameNotFoundException("User Name is not Found");
		}
		return userDetails(ut);
	}

	private UserDetails userDetails(Utilisateur ui) {
		return User.withUsername(ui.getMail()).password(ui.getMdp()).authorities(getAuthorities(ui)).build();
	}

	public Collection<? extends GrantedAuthority> getAuthorities(Utilisateur ui) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (StringUtils.isNotEmpty(ui.getRole())) {
			String[] roles = ui.getRole().split(",");
			for (String r : roles) {
				// roles should start with 'ROLE_'. Example: 'ROLE_ADMIN', 'ROLE_VOTE'
				authorities.add(new SimpleGrantedAuthority(r));
			}
		}
		return authorities;

	}
}
