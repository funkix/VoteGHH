package com.gesthelp;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.gesthelp.vote.domain.Question;
import com.gesthelp.vote.domain.QuestionItem;
import com.gesthelp.vote.domain.QuestionResp;
import com.gesthelp.vote.domain.QuestionRespItem;
import com.gesthelp.vote.domain.Scrutin;
import com.gesthelp.vote.repository.QuestionRespRepository;
import com.gesthelp.vote.service.ScrutinService;

@SpringBootApplication
@ComponentScan("com.gesthelp.vote")
@EnableBatchProcessing
@Transactional
public class HashTestApp implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(HashTestApp.class, args));
	}

	@Autowired
	ScrutinService service;

	@Autowired
	QuestionRespRepository repo;

	private String buildVoteImage(List<QuestionResp> checkVotesList) {
		StringBuffer resp = new StringBuffer();
		for (QuestionResp r : checkVotesList) {
			resp.append("?").append(r.getQuestion().getQuestiontext()).append("\n");
			for (QuestionRespItem ri : r.getChecked()) {
				resp.append("-").append(ri.getCheckedItem().getQuestiontext()).append("\n");
			}
		}
		return resp.toString();
	}

	private String buildScrutinImage(Scrutin s) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(s.getNature());
		for (Question q : s.getQuestions()) {
			buffer.append("?").append(q.getQuestiontext().toLowerCase().trim()).append("\n");
			for (QuestionItem option : q.getChoices()) {
				buffer.append("-").append(option.getQuestiontext().toLowerCase().trim()).append("\n");
			}
		}
		return buffer.toString();
	}

	@Override
	public void run(String... args) throws Exception {
		String scrutinBuffer = buildScrutinImage(service.findScrutinScrut(1l, 1l));
		System.out.println("******* scrutinBuffer :\n\n" + scrutinBuffer);
		System.out.println("******* sha256hex= " + DigestUtils.sha256Hex(scrutinBuffer));
		System.out.println("******* md5Hex= " + DigestUtils.md5Hex(scrutinBuffer));
		String scrutinVoteBuffer = buildVoteImage(repo.userScrutinVotes(1l, 1l));
		System.out.println("******* scrutinVoteBuffer :\n\n" + scrutinVoteBuffer);
		System.out.println("******* sha256hex= " + DigestUtils.sha256Hex(scrutinVoteBuffer));
		System.out.println("******* md5Hex= " + DigestUtils.md5Hex(scrutinVoteBuffer));
	}

}
