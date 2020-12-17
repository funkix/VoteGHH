package com.gesthelp.vote.batch.mail.voteconfirm;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gesthelp.vote.batch.dummy.Commons;
import com.gesthelp.vote.domain.ScrutinVote;
import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.EmailSender;
import com.gesthelp.vote.service.ScrutinService;
import com.gesthelp.vote.service.UtilisateurEmailService;
import com.gesthelp.vote.service.UtilisateurService;

import lombok.extern.java.Log;

@Log
@Component
public class SendEmailTasklet implements Tasklet, StepExecutionListener, Commons {

	private Long utilisateurId, scrutinId;

	@Autowired
	private ScrutinService scrutinService;

	@Autowired
	private UtilisateurService utilisateurService;

	@Autowired
	private UtilisateurEmailService utilisateurEmailService;

	@Value("${gesthelp.mail.voteconfirm.message}")
	private String mailMessage;
	@Value("${gesthelp.mail.voteconfirm.subject}")
	private String mailSubject;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("execute IN");
		// Scrutin scrutin = scrutinService.findScrutinVotant(utilisateurId, scrutinId);
		ScrutinVote sv = scrutinService.findScrutinVote(utilisateurId, scrutinId);
		if (sv == null) {
			throw new RuntimeException("vote absent pour utilisateurId=" + utilisateurId + ", scrutinId=" + scrutinId);
		}
		if (sv.getVoteDate() == null || sv.getVoteHash() == null) {
			throw new RuntimeException("vote non réalisé pour utilisateurId=" + utilisateurId + ", scrutinId=" + scrutinId);
		}
		Utilisateur utilisateur = this.utilisateurService.findById(this.utilisateurId);
		utilisateurEmailService.sendMessageAVote(utilisateur, sv.getVoteHash());
		return RepeatStatus.FINISHED;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.utilisateurId = (Long) stepExecution.getJobParameters().getLong(UTILISATEUR_ID_KEY);
		this.scrutinId = (Long) stepExecution.getJobParameters().getLong(SCRUTIN_ID_KEY);
		log.info("beforeStep scrutinId=" + scrutinId + " utilisateurId=" + utilisateurId);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}
