package com.autobots.automanager.controles;

import com.autobots.automanager.jwt.ProvedorJwt;
import com.autobots.automanager.modelos.LoginRequest;
import com.autobots.automanager.modelos.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoControle {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ProvedorJwt provedorJwt;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            @RequestHeader(value = "Authorization", required = false) String tokenExistente) {
        try {
            // Verifica se existe um token no header
            if (tokenExistente != null && tokenExistente.startsWith("Bearer ")) {
                String token = tokenExistente.substring(7); // Remove o prefixo "Bearer "

                // Verifica se o token é válido
                if (provedorJwt.validarJwt(token)) {
                    // Retorna o token válido, sem necessidade de nova autenticação
                    return ResponseEntity.ok(new LoginResponse(token));
                }
            }

            // Autentica as credenciais do usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Gera um novo token JWT
            String novoToken = provedorJwt.proverJwt(authentication.getName());

            // Retorna o novo token e as informações do usuário no corpo da resposta
            return ResponseEntity.ok(new LoginResponse(novoToken));

        } catch (AuthenticationException e) {
            // Retorna UNAUTHORIZED em caso de falha de autenticação
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Credenciais inválidas"));
        } catch (Exception e) {
            // Retorna BAD_REQUEST para qualquer erro inesperado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse("Erro ao processar a requisição"));
        }
    }
}