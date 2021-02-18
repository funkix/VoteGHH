package com.gesthelp.vote.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.service.SecurityRoles;
import com.gesthelp.vote.service.UserCacheService;

import lombok.extern.java.Log;

@Log
@Service
public class ScrutateurLogoutHandler implements LogoutHandler {

	@Autowired
	private UserCacheService cache;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		log.info("logout " + authentication);
		Long l = authentication.getAuthorities().stream().filter(a -> a.getAuthority().equals(SecurityRoles.ROLE_SCRUTATEUR)).count();
		if (l.equals(1l)) {
			this.cache.evictUser(authentication);
		}
	}

}
