package com.gesthelp.vote.web;

import java.util.logging.Level;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gesthelp.vote.service.exception.DejaVoteException;
import com.gesthelp.vote.service.exception.VoteRuntimeException;

import lombok.extern.java.Log;

@ControllerAdvice
@Log
public class ExceptionController {

	@ExceptionHandler({ DejaVoteException.class, })
	public String revoteError(DejaVoteException e) {
		log.log(Level.WARNING, "DejaVote ! (should not happen):", e);
		return "error/revoteError";
	}

	@ExceptionHandler({ VoteRuntimeException.class, })
	public String runtimeError(VoteRuntimeException e) {
		log.log(Level.SEVERE, "unhandled VoteRuntimeException :", e);
		return "error/runtimeError";
	}

	@ExceptionHandler({ Exception.class, })
	public String error(Exception e) {
		log.log(Level.SEVERE, "unhandled excception :", e);
		return "error/runtimeError";
	}
}
