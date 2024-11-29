package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkCredencialCodigoDeBarra;
import com.autobots.automanager.modelos.AdicionarLinkCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioCredencialCodigoBarra;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/credencial")
public class CredencialControle {
	
	@Autowired
	private RepositorioCredencialUsuarioSenha repositorioCredencialUsuarioSenha;
	@Autowired
	private RepositorioCredencialCodigoBarra repositorioCredencialCodigoBarra;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private AdicionarLinkCredencialUsuarioSenha adicionarLinkCredencialUserSenha;
	@Autowired
	private AdicionadorLinkCredencialCodigoDeBarra adicionarLinkCredencialCodigoDeBarra;

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/listar")
	public ResponseEntity<?> buscarCredenciaisUsuariosSenhas(){
		// Este método recupera todas as credenciais de usuário e as retorna na entidade de resposta

		List<CredencialUsuarioSenha> credenciais = repositorioCredencialUsuarioSenha.findAll();
		
		if(!credenciais.isEmpty()) {
			adicionarLinkCredencialUserSenha.adicionarLink(credenciais);
			for(CredencialUsuarioSenha credencial: credenciais) {
				adicionarLinkCredencialUserSenha.adicionarLinkUpdate(credencial);
				adicionarLinkCredencialUserSenha.adicionarLinkDelete(credencial);
			}
			return new ResponseEntity<List<CredencialUsuarioSenha>>(credenciais, HttpStatus.FOUND);
		}

		return new ResponseEntity<String>("Nenhuma credencial encontrada...", HttpStatus.NOT_FOUND);
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/consultar/{id}")
	public ResponseEntity<?> buscarCredencialUsuarioSenhaPorId(@PathVariable Long id){
		// Este método recupera uma credencial de usuário por ID e a retorna na entidade de resposta

		CredencialUsuarioSenha credencial = repositorioCredencialUsuarioSenha.findById(id).orElse(null);

		if(credencial == null) {
			return new ResponseEntity<String>("Credencial não encontrada...", HttpStatus.NOT_FOUND);
		}
		
		adicionarLinkCredencialUserSenha.adicionarLink(credencial);
		adicionarLinkCredencialUserSenha.adicionarLinkUpdate(credencial);
		adicionarLinkCredencialUserSenha.adicionarLinkDelete(credencial);

		return new ResponseEntity<CredencialUsuarioSenha>(credencial, HttpStatus.FOUND);
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PostMapping("/cadastrar/{idUsuario}")
	public ResponseEntity<?> cadastrarCredencialUserSenha(@RequestBody CredencialUsuarioSenha dados, @PathVariable Long idUsuario){
		// Este método cadastra uma credencial de usuário e a retorna na entidade de resposta

		Usuario usuario = repositorioUsuario.findById(idUsuario).orElse(null);

		if(usuario == null) {
			return new ResponseEntity<String>("Usuario não encontrado!",HttpStatus.NOT_FOUND);
		}

		List<CredencialUsuarioSenha> credenciais = repositorioCredencialUsuarioSenha.findAll();
		Boolean verificador = false;
		for(CredencialUsuarioSenha credencial: credenciais) {
			if(dados.getNomeUsuario().equals(credencial.getNomeUsuario())) {
				verificador = true;
			}
		}

		if (verificador == true) {
			return new ResponseEntity<String>("Credencial já existente!",HttpStatus.CONFLICT);
		}

		dados.setCriacao(new Date());
		usuario.getCredenciais().add(dados);
		repositorioUsuario.save(usuario);

		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
			
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PutMapping("/atualizar/{idCredencial}")
	public ResponseEntity<?> atualizarCredencialUserSenha(@PathVariable Long idCredencial, @RequestBody CredencialUsuarioSenha dados){
		// Este método atualiza uma credencial de usuário e a retorna na entidade de resposta

		CredencialUsuarioSenha credencial = repositorioCredencialUsuarioSenha.findById(idCredencial).orElse(null);

		if(credencial == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}

		if(dados != null) {
			if(dados.getNomeUsuario() != null) {
				credencial.setNomeUsuario(dados.getNomeUsuario());
			}

			if(dados.getSenha() != null) {
				credencial.setSenha(dados.getSenha());
			}
			repositorioCredencialUsuarioSenha.save(credencial);
		}

		return new ResponseEntity<>(credencial, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@DeleteMapping("/excluir/{idCredencial}")
	public ResponseEntity<?> excluirCredencialUserSenha(@PathVariable Long idCredencial){
		// Este método exclui uma credencial de usuário e a retorna na entidade de resposta

		CredencialUsuarioSenha verificacao = repositorioCredencialUsuarioSenha.findById(idCredencial).orElse(null);

		if(verificacao == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}
			
		for(Usuario usuario:repositorioUsuario.findAll()) {
			if(!usuario.getCredenciais().isEmpty()) {
				for(Credencial credencial: usuario.getCredenciais()) {
					if(credencial.getId() == idCredencial) {
						usuario.getCredenciais().remove(credencial);
						repositorioUsuario.save(usuario);
						break;
					}
				}
			}
		}
		
		return new ResponseEntity<>("Credencial excluida com sucesso!", HttpStatus.ACCEPTED);
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/listar-codigo-barra")
	public ResponseEntity<?> buscarCredenciaisCodigoBarras(){
		// Este método recupera todas as credenciais de código de barras e as retorna na entidade de resposta

		List<CredencialCodigoBarra> credenciais = repositorioCredencialCodigoBarra.findAll();
		adicionarLinkCredencialCodigoDeBarra.adicionarLink(credenciais);
		for(CredencialCodigoBarra credencial: credenciais) {
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkUpdate(credencial);
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkDelete(credencial);
		}

		return new ResponseEntity<List<CredencialCodigoBarra>>(credenciais, HttpStatus.FOUND);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/consultar-codigo-barra/{id}")
	public ResponseEntity<?> buscarCredencialCodigoBarraPorId(@PathVariable Long id){
		// Este método recupera uma credencial de código de barras por ID e a retorna na entidade de resposta

		CredencialCodigoBarra credencial = repositorioCredencialCodigoBarra.findById(id).orElse(null);

		if(credencial == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}

		adicionarLinkCredencialCodigoDeBarra.adicionarLink(credencial);
		adicionarLinkCredencialCodigoDeBarra.adicionarLinkUpdate(credencial);
		adicionarLinkCredencialCodigoDeBarra.adicionarLinkDelete(credencial);

		return new ResponseEntity<CredencialCodigoBarra>(credencial, HttpStatus.FOUND);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PostMapping("/cadastrar-codigo-barra/{idUsuario}")
	public ResponseEntity<?> cadastrarCredencialCodigoBarra(@RequestBody CredencialCodigoBarra dados, @PathVariable Long idUsuario){
		// Este método cadastra uma credencial de código de barras e a retorna na entidade de resposta

		Usuario usuario = repositorioUsuario.findById(idUsuario).orElse(null);

		if(usuario == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}

		List<CredencialCodigoBarra> credenciais = repositorioCredencialCodigoBarra.findAll();
		Boolean verificador = false;
		for(CredencialCodigoBarra credencial: credenciais) {
			if(dados.getCodigo() == credencial.getCodigo()) {
				verificador = true;
			}
		}

		if (verificador == true) {
			return new ResponseEntity<String>("Credencial já existente!",HttpStatus.CONFLICT);
		}

		double randomNumero = Math.random();
		dados.setCodigo(randomNumero);
		dados.setCriacao(new Date());
		usuario.getCredenciais().add(dados);
		repositorioUsuario.save(usuario);

		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PutMapping("/atualizar-codigo-barra/{idCredencial}")
	public ResponseEntity<?> atualizarCredencialCodigoBarra(@PathVariable Long idCredencial, @RequestBody CredencialCodigoBarra dados){
		// Este método atualiza uma credencial de código de barras e a retorna na entidade de resposta

		CredencialCodigoBarra credencial = repositorioCredencialCodigoBarra.findById(idCredencial).orElse(null);

		if(credencial == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}

		if(dados != null) {
			credencial.setCodigo(dados.getCodigo());
			repositorioCredencialCodigoBarra.save(credencial);
		}
		
		return new ResponseEntity<>(credencial, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@DeleteMapping("/excluir-codigo-barra/{idCredencial}")
	public ResponseEntity<?> excluirCredencialCodigoBarra(@PathVariable Long idCredencial){
		// Este método exclui uma credencial de código de barras e a retorna na entidade de resposta
		
		CredencialCodigoBarra verificacao = repositorioCredencialCodigoBarra.findById(idCredencial).orElse(null);

		if(verificacao == null) {
			return new ResponseEntity<String>("Credencial não encontrada!", HttpStatus.NOT_FOUND);
		}

		for(Usuario usuario:repositorioUsuario.findAll()) {
			if(!usuario.getCredenciais().isEmpty()) {
				for(Credencial credencial: usuario.getCredenciais()) {
					if(credencial.getId() == idCredencial) {
						usuario.getCredenciais().remove(credencial);
						repositorioUsuario.save(usuario);
						break;
					}
				}
			}
		}
		
		return new ResponseEntity<>("Credencial excluida com sucesso!", HttpStatus.ACCEPTED);
	}
}
