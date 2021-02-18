package com.gesthelp.vote.service;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthentication {
	Authentication authentication;
	LocalDateTime logTime;
}
