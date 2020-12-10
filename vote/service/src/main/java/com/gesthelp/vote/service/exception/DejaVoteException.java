package com.gesthelp.vote.service.exception;

public class DejaVoteException extends VoteRuntimeException {

	public DejaVoteException() {
		super("revote", "deja vote");
	}

}
