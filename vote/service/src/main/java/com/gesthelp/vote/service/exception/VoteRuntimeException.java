package com.gesthelp.vote.service.exception;

public class VoteRuntimeException extends RuntimeException {
	String code;

	public VoteRuntimeException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public VoteRuntimeException(Throwable cause) {
		super(cause);
	}

	public VoteRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public VoteRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
