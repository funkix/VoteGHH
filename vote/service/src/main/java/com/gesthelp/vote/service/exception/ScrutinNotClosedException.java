package com.gesthelp.vote.service.exception;

public class ScrutinNotClosedException extends VoteRuntimeException {

	public ScrutinNotClosedException() {
		super("revote", "deja vote");
	}

}
