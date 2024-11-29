package com.comunicacao.api.controles;

import java.util.ArrayList;
import java.util.List;

import com.comunicacao.api.entidades.Empresa;
import com.comunicacao.api.entidades.Usuario;
import com.comunicacao.api.jwt.ProvedorJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

@RestController
public class ControleFuncionario {

	@Autowired
	private ProvedorJwt provedorJwt;

	private static final String BASE_URL = "http://localhost:8080";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/usuarios-empresa/{empresaId}")
	public ResponseEntity<?> obterUsuarios(@PathVariable("empresaId") Long empresaId,
										   @RequestHeader(value = "Authorization", required = false) String tokenExistente) {

		if (tokenExistente != null && tokenExistente.startsWith("Bearer ")) {
			String token = tokenExistente.substring(7);
			if (!provedorJwt.validarJwt(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido ou ausente.");
			}

			String url = buildUrl("/empresa/consultar/" + empresaId);
			HttpEntity<Void> entity = new HttpEntity<>(createHeaders(token));
			try {
				ResponseEntity<Empresa> response = executeRequest(url, entity, new ParameterizedTypeReference<>() {});
				
				return ResponseEntity.ok(response.getBody().getUsuarios());
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter usuários da empresa " + e);
			}

			return ResponseEntity.ok("Usuários da empresa " + empresaId);
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido ou ausente.");
	}

	private String buildUrl(String endpoint, Object... uriVariables) {
		return UriComponentsBuilder.fromHttpUrl(BASE_URL + endpoint)
				.buildAndExpand(uriVariables)
				.toUriString();
	}

	private <T> ResponseEntity<T> executeRequest(String url, HttpEntity<?> entity, ParameterizedTypeReference<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
	}

	private HttpHeaders createHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.replace("Bearer ", ""));
		return headers;
	}
}