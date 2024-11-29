package com.comunicacao.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.util.Date;

class AnalisadorJwt {
	private String assinatura;
	private String jwt;

	public AnalisadorJwt(String assinatura, String jwt) {
		this.assinatura = assinatura;
		this.jwt = jwt;
	}

	public Claims obterReivindicacoes() {
		try {
			Claims claims = Jwts.parser().setSigningKey(assinatura.getBytes()).parseClaimsJws(jwt).getBody();
			Date expiration = claims.getExpiration();
			if (expiration != null && expiration.before(new Date())) {
				throw new ExpiredJwtException(null, claims, "Token expired");
			}
			return claims;
		} catch (ExpiredJwtException e) {
			System.out.println("Token expired: " + e.getMessage());
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String obterNomeUsuairo(Claims reivindicacoes) {
		if (reivindicacoes != null) {
			String nomeUsuario = reivindicacoes.getSubject();
			return nomeUsuario;
		}
		return null;
	}
}
