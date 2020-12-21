package com.gesthelp.vote.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseController implements SessionKey {

	private final String SESSION_KEY_PREFIX = "_";
	private final String SESSION_KEY_USER_ID = "_user_id";

	@Autowired
	protected HttpSession session;
	@Autowired
	protected HttpServletRequest request;

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

	/*
	 * Gestion des messages
	 */
	protected void addFlashMessage(String message, UIMessageType messageTypeRef) {
		addFlashMessage(message, messageTypeRef, false);
	}

	@SuppressWarnings("unchecked")
	protected void addFlashMessage(String message, UIMessageType messageTypeRef, Boolean sessionScope) {
		UIMessage uimessage = new UIMessage();
		uimessage.setMessage(message);
		uimessage.setMessageTypeRef(messageTypeRef);
		if (sessionScope == null || !sessionScope) {
			List<UIMessage> uiMessageList = (List<UIMessage>) request.getAttribute("uiMessageList");
			if (uiMessageList == null) {
				uiMessageList = new ArrayList<UIMessage>();
			}
			uiMessageList.add(uimessage);
			request.setAttribute("uiMessageList", uiMessageList);
		} else {
			List<UIMessage> uiMessageList = (List<UIMessage>) session.getAttribute("uiMessageList");
			if (uiMessageList == null) {
				uiMessageList = new ArrayList<UIMessage>();
			}
			uiMessageList.add(uimessage);
			session.setAttribute("uiMessageList", uiMessageList);
		}

	}
}
