package com.gesthelp.vote.web;

public enum UIMessageType {
	INFO("INF"), ERREUR("ERR"), WARN("WARN");
	private final String value;

	private UIMessageType(String val) {
		this.value = val;
	}

	public String getValue() {
		return value;
	}

	public static UIMessageType fromValue(String value) {
		if (value != null) {
			value = value.trim().toUpperCase();
			if (value.equals(INFO.value)) {
				return INFO;
			} else if (value.equals(ERREUR.value)) {
				return ERREUR;
			} else if (value.equals(WARN.value)) {
				return WARN;
			}
		}
		throw new RuntimeException("Valeur de r�f�rence incorrecte: " + value);
	}

}
