package com.gesthelp.vote.web;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gesthelp.vote.service.exception.DejaVoteException;
import com.gesthelp.vote.service.exception.VoteRuntimeException;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler({ DejaVoteException.class, })
	public String revoteError() {
		return "error/revoteError";
	}

	@ExceptionHandler({ VoteRuntimeException.class, })
	public String runtimeError() {
		return "error/runtimeError";
	}

	@ExceptionHandler({ Exception.class, })
	public String error() {
		return "error/runtimeError";
	}
}
