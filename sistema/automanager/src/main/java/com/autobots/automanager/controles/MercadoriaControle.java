package com.autobots.automanager.controles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkMercadoria;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {
	
	@Autowired
	private RepositorioMercadoria repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioVenda repositorioVenda;
	@Autowired
	private AdicionadorLinkMercadoria adicionarLink;

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/listar")
	public ResponseEntity<List<Mercadoria>> buscarMercadorias(){
		// Busca todas as mercadorias do repositório

		List<Mercadoria> mercadorias = repositorio.findAll();
		List<Mercadoria> novaListaMercadoria = new ArrayList<Mercadoria>();

		for(Mercadoria mercadoriaRegistrada: mercadorias) {
			if(mercadoriaRegistrada.getOriginal() != null) {				
				if(mercadoriaRegistrada.getOriginal() == true) {
					adicionarLink.adicionarLinkUpdate(mercadoriaRegistrada);
					adicionarLink.adicionarLinkDelete(mercadoriaRegistrada);
					novaListaMercadoria.add(mercadoriaRegistrada);
				}
			}
		}

		adicionarLink.adicionarLink(novaListaMercadoria);
		return new ResponseEntity<List<Mercadoria>>(mercadorias, HttpStatus.FOUND);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@GetMapping("/consultar/{id}")
	public ResponseEntity<Mercadoria> buscarMercadoria(@PathVariable Long id){
		// Busca uma mercadoria específica do repositório

		Mercadoria mercadoria = repositorio.findById(id).orElse(null);
		HttpStatus status = null;

		if(mercadoria == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(mercadoria);
			adicionarLink.adicionarLinkUpdate(mercadoria);
			adicionarLink.adicionarLinkDelete(mercadoria);
			status = HttpStatus.FOUND;
		}

		return new ResponseEntity<Mercadoria>(mercadoria,status);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PostMapping("/cadastrar/{idEmpresa}")
	public ResponseEntity<Empresa> cadastrarMercadoriaEmpresa(@RequestBody Mercadoria dados, @PathVariable Long idEmpresa){
		// Cadastra uma mercadoria para uma empresa específica

		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		dados.setOriginal(true);
		dados.setCadastro(new Date());
		HttpStatus status = null;

		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			empresa.getMercadorias().add(dados);
			repositorioEmpresa.save(empresa);
			for(Mercadoria mercadoria: empresa.getMercadorias()) {
				adicionarLink.adicionarLink(mercadoria);
				adicionarLink.adicionarLinkUpdate(mercadoria);
				adicionarLink.adicionarLinkDelete(mercadoria);
			}
			status = HttpStatus.CREATED;
		}

		return new ResponseEntity<Empresa>(empresa,status);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PostMapping("/cadastrar-mercadoria/{idUsuarioFornecedor}")
	public ResponseEntity<?> cadastrarMercadoriaFornecedor(@RequestBody Mercadoria dados, @PathVariable Long idUsuarioFornecedor){
		// Cadastra uma mercadoria para um fornecedor específico

		Usuario usuario = repositorioUsuario.findById(idUsuarioFornecedor).orElse(null);
		dados.setOriginal(true);
		dados.setCadastro(new Date());
		
		if(usuario == null) {
			return new ResponseEntity<>("Usuario não encontrado!",HttpStatus.NOT_FOUND);
		}

		usuario.getMercadorias().add(dados);
		repositorioUsuario.save(usuario);
		for(Mercadoria mercadoria: usuario.getMercadorias()) {
			adicionarLink.adicionarLink(mercadoria);
			adicionarLink.adicionarLinkUpdate(mercadoria);
			adicionarLink.adicionarLinkDelete(mercadoria);
		}
		return new ResponseEntity<>(usuario,HttpStatus.CREATED);
		
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@PutMapping("/atualizar/{idMercadoria}")
	public ResponseEntity<?> atualizarMercadoria(@PathVariable Long idMercadoria, @RequestBody Mercadoria dados){
		// Atualiza uma mercadoria específica

		Mercadoria mercadoria = repositorio.findById(idMercadoria).orElse(null);
		if(mercadoria == null) {
			return new ResponseEntity<>("Mercadoria não encontrada!",HttpStatus.NOT_FOUND);
		}

		if(dados != null) {
			if(dados.getNome() != null) {
				mercadoria.setNome(dados.getNome());
			}
			if(dados.getDescricao() != null) {
				mercadoria.setDescricao(dados.getDescricao());
			}
			if(dados.getQuantidade() == 0) {
				mercadoria.setQuantidade(dados.getQuantidade());
			}
			if(dados.getValidade() != null) {
				mercadoria.setValidade(dados.getValidade());
			}
			if(dados.getFabricao() != null) {
				mercadoria.setFabricao(dados.getFabricao());
			}
			
			mercadoria.setValor(dados.getValor());
			repositorio.save(mercadoria);
		}
		return new ResponseEntity<>(mercadoria, HttpStatus.ACCEPTED);
	
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
	@DeleteMapping("/excluir/{idMercadoria}")
	public ResponseEntity<?> excluirMercadoriaEmpresa(@PathVariable Long idMercadoria){
		// Exclui uma mercadoria específica

		List<Empresa> empresas = repositorioEmpresa.findAll();
		List<Usuario> usuarios = repositorioUsuario.findAll();
		List<Venda> vendas = repositorioVenda.findAll();
		Mercadoria validacao = repositorio.findById(idMercadoria).orElse(null);
		
		if(validacao == null) {
			return new ResponseEntity<>("Mercadoria não encontrada!",HttpStatus.NOT_FOUND);
		}
			
		for(Empresa empresa: repositorioEmpresa.findAll()) {
			if(!empresa.getMercadorias().isEmpty()) {
				for(Mercadoria mercadoriaEmpresa: empresa.getMercadorias()) {
					if(mercadoriaEmpresa.getId() == idMercadoria) {
						for(Empresa empresaRegistrada: empresas) {
							empresaRegistrada.getMercadorias().remove(mercadoriaEmpresa);
						}
					}
				}
			}
		}
		
		for(Usuario usuario: repositorioUsuario.findAll()) {
			if(!usuario.getMercadorias().isEmpty()) {
				for(Mercadoria mercadoriaUsuario:usuario.getMercadorias()) {
					if(mercadoriaUsuario.getId() == idMercadoria) {
						for(Usuario usuarioRegistrado: usuarios) {
							usuarioRegistrado.getMercadorias().remove(mercadoriaUsuario);
						}
					}
				}
			}
		}
		

		for(Venda venda: repositorioVenda.findAll()) {
			if(!venda.getMercadorias().isEmpty()) {
				for(Mercadoria mercadoriaVenda: venda.getMercadorias()) {
					if(mercadoriaVenda.getId() == idMercadoria) {
						for(Venda vendaRegistrada:vendas) {
							vendaRegistrada.getMercadorias().remove(mercadoriaVenda);
						}
					}
				}
			}
		}

		empresas = repositorioEmpresa.findAll();
		usuarios = repositorioUsuario.findAll();
		vendas = repositorioVenda.findAll();
		repositorio.deleteById(idMercadoria);
		
		return new ResponseEntity<>("Mercadoria excluida com sucesso!",HttpStatus.ACCEPTED);
		
	}

}
