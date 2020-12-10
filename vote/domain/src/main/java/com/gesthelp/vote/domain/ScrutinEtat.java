package com.gesthelp.vote.domain;


public enum ScrutinEtat {

	CREATION(0), RECETTE(1), PRODUCTION(2), DEPOUILLE(3);

	private int value;

	private ScrutinEtat(int val) {
		this.value = val;
	}

	public int value() {
		return this.value;
	}

	public static ScrutinEtat fromValue(int val) {
		switch (val) {
		case 0:
			return ScrutinEtat.CREATION;
		case 1:
			return ScrutinEtat.RECETTE;
		case 2:
			return ScrutinEtat.PRODUCTION;
		case 3:
			return ScrutinEtat.DEPOUILLE;
		default:
			throw new RuntimeException("unacceptatble ScrutinEtat value: " + val);
		}
	}
}
