package com.comunicacao.api.filtros;

class ValidadorCabecalho {
	private String cabecalho;

	public ValidadorCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}

	public boolean validar() {
		if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
			return true;
		} else {
			return false;
		}
	}

}