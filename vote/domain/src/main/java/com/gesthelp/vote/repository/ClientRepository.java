package com.gesthelp.vote.repository;

import org.springframework.data.repository.CrudRepository;

import com.gesthelp.vote.domain.Client;
public interface ClientRepository extends CrudRepository<Client, Long> {

  Client findByNom(String nom);

  Client findById(long id);
}
