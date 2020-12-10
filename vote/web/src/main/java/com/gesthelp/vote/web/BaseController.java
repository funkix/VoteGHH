package com.gesthelp.vote.web;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseController implements SessionKey {

	private final String SESSION_KEY_PREFIX = "_";
	private final String SESSION_KEY_USER_ID = "_user_id";

	@Autowired
	protected HttpSession session;

	protected void setUserId(Long id) {
		this.session.setAttribute(SESSION_KEY_USER_ID, id);
	}

	protected Long getUserId() {
		return (Long) session.getAttribute(SESSION_KEY_USER_ID);
	}

	protected void setSessionObject(String key, Object obj) {
		this.session.setAttribute(SESSION_KEY_PREFIX + key.toUpperCase(), obj);
	}

	protected Object getSessionObject(String key) {
		return session.getAttribute(SESSION_KEY_PREFIX + key.toUpperCase());
	}

}
