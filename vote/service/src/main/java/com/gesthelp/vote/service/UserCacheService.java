package com.gesthelp.vote.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Utilisateur;

import lombok.extern.java.Log;

@Service
@Log
public class UserCacheService {

	@PersistenceContext
	private EntityManager entityManager;

	private final ConcurrentMap<Long, ConcurrentMap<String, UserAuthentication>> store = new ConcurrentHashMap<>();

	public void storeUser(Long scrutinId, Authentication user) {
		log.info("storeUser " + user);
		ConcurrentMap<String, UserAuthentication> scrutStore = store.get(scrutinId);
		if (scrutStore == null) {
			scrutStore = new ConcurrentHashMap<>(256);
			store.put(scrutinId, scrutStore);
		}
		scrutStore.put(user.getName(), new UserAuthentication(user, LocalDateTime.now()));
	}

	public void evictUser(Authentication user) {
		log.info("evictUser " + user);
		Utilisateur u = (Utilisateur) entityManager.createQuery("from Utilisateur where mail=:mail", Utilisateur.class)
				.setParameter("mail", user.getName()).getSingleResult();
		BigInteger scrutId = (BigInteger) entityManager.createNativeQuery("select scrutin_id from utilisateur_scrutin where utilisateur_id=:id")
				.setParameter("id", u.getId()).getSingleResult();
		log.info("evicting tUser " + u.getId() + " from scrutin " + scrutId);
		this.evictUser(scrutId.longValue(), user);
	}

	private void evictUser(Long scrutinId, Authentication user) {
		ConcurrentMap<String, UserAuthentication> scrutStore = store.get(scrutinId);
		if (scrutStore == null) {
			log.warning("evictUser" + user.getName() + " from scrutin " + scrutinId + " : user was not stored");
		} else {
			UserAuthentication evicted = scrutStore.remove(user.getName());
			if (evicted == null) {
				log.warning("evictUser" + user.getName() + " from scrutin " + scrutinId + " : user was not stored");
			} else {
				log.info("evictUser" + user.getName() + " from scrutin " + scrutinId + " ok");
			}
		}
	}

	public List<UserAuthentication> listUsers(Long scrutinId) {
		ConcurrentMap<String, UserAuthentication> scrutStore = store.get(scrutinId);
		if (scrutStore == null) {
			return new ArrayList<UserAuthentication>(0);
		}
		return new ArrayList<>(scrutStore.values());
	}

}
