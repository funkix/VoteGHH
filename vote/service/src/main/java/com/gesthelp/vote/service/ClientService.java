package com.gesthelp.vote.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Client;
import com.gesthelp.vote.repository.ClientRepository;

import lombok.extern.java.Log;

@Service
@Log
public class ClientService {
	@Autowired
	private ClientRepository repo;

	public Client findClient(String name) {
		log.info("findClient IN " + name);
		return this.repo.findByNom(name);
	}

	public Client insertClient(String nom, String mail) {
		log.info("insertClient IN " + nom + ", mail=" + mail);
		Client c = new Client();
		c.setNom(nom);
		c.setMail(mail);
		return repo.save(c);
	}
}
