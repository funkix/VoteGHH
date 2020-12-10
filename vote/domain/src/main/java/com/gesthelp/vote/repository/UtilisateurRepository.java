package com.gesthelp.vote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gesthelp.vote.domain.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
	Utilisateur findByMail(String mail);

	@Query(nativeQuery = true, value = "select u.* from scrutin s, utilisateur_scrutin us, utilisateur u  where us.scrutin_id=s.id and us.utilisateur_id=u.id and s.id=:scrutinId and u.role like :roleName")
	List<Utilisateur> findByScrutinIdAndRole(@Param("scrutinId") Long scrutinId, @Param("roleName") String roleName);

}
