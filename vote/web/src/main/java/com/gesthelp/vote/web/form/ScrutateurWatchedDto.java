package com.gesthelp.vote.web.form;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ScrutateurWatchedDto {
	String userMail;
	LocalDateTime logTime;
}
