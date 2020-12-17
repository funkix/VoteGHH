package com.gesthelp.vote.batch.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class SendEmailAVoteJob extends BaseLauncher {
	@Autowired
	private Job sendVoteConfirmMailJob;

	public void launch(Long utilisateurId, Long scrutinId) {
		log.info("launch IN utilisateurId=" + utilisateurId + ", scrutinId=" + scrutinId);
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addLong(UTILISATEUR_ID_KEY, utilisateurId);
		builder.addLong(SCRUTIN_ID_KEY, scrutinId);
		super.launch(sendVoteConfirmMailJob, builder, true);
	}
}
