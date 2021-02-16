package com.gesthelp.vote.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gesthelp.vote.domain.Scrutin;

public interface ScrutinRepository extends JpaRepository<Scrutin, Long> {
	@Query(nativeQuery = true, value = "select s.* from scrutin s, utilisateur_scrutin u  where u.scrutin_id=s.id and s.phase=:phase and u.utilisateur_id=:userId and s.date_heure_debut<=:date and s.date_heure_fin>=:date")
	List<Scrutin> listUserScrutins(@Param("userId") Long userId, @Param("date") Date date, @Param("phase") int phase);

	@Query(nativeQuery = true, value = "select s.* from scrutin s, utilisateur_scrutin u  where u.scrutin_id=s.id and s.phase=:phase and u.utilisateur_id=:userId")
	List<Scrutin> listUserScrutinsNoDate(@Param("userId") Long userId, @Param("phase") int phase);

	@Query(nativeQuery = true, value = "select s.* from scrutin s, utilisateur_scrutin u  where u.scrutin_id=s.id and s.phase=:phase and u.utilisateur_id=:userId and s.date_heure_debut<=:date and s.date_heure_fin>=:date and s.id=:scrutinId")
	Scrutin findScrutinVotant(@Param("userId") Long userId, @Param("scrutinId") Long scrutinId, @Param("date") Date date, @Param("phase") int phase);

	@Query(nativeQuery = true, value = "select s.* from scrutin s, utilisateur_scrutin u  where u.scrutin_id=s.id and s.phase=:phase and u.utilisateur_id=:userId and s.id=:scrutinId")
	Scrutin findScrutinVotantNoDate(@Param("userId") Long userId, @Param("scrutinId") Long scrutinId, @Param("phase") int phase);

	@Modifying(clearAutomatically = true)
	@Query("update ScrutinVote sv set sv.voteDate=:date, sv.voteHash=:hash where sv.id.scrutinId=:scrutinId and sv.id.utilisateurId=:userId")
	int setScrutinVoteDate(@Param("userId") Long userId, @Param("scrutinId") Long scrutinId, @Param("date") Date date, @Param("hash") String hash);
	
	/**
	 * @param userId
	 * @return tous les scrutins associés à un utilisateur 'admin' (qq soit l'état
	 *         ou dates des scrutins)
	 */
	@Query(nativeQuery = true, value = "select s.* from scrutin s, utilisateur_scrutin u  where u.scrutin_id=s.id and u.utilisateur_id=:userId")
	List<Scrutin> listAdminUserScrutins(@Param("userId") Long userId);

}
