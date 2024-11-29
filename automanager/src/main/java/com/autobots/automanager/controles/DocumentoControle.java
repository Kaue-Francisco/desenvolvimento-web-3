package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkDocumento;
import com.autobots.automanager.repositorios.RepositorioDocumento;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {

	@Autowired
	private RepositorioDocumento repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private AdicionadorLinkDocumento adicionarLink;
	
	@GetMapping("/listar")
	public ResponseEntity<List<Documento>> buscarDocumentos(){
		// Retorna uma lista com todos os documentos cadastrados

		List<Documento> documentos = repositorio.findAll();
		adicionarLink.adicionarLink(documentos);

		if(!documentos.isEmpty()) {		
			for(Documento documento: documentos) {
				adicionarLink.adicionarLinkUpdate(documento);
				adicionarLink.adicionarLinkDelete(documento);
			}
		}
		
		return new ResponseEntity<List<Documento>>(documentos,HttpStatus.FOUND);
	}
	
	@GetMapping("/consultar/{id}")
	public ResponseEntity<Documento> buscarDocumento(@PathVariable Long id){
		// Busca o documento pelo ID fornecido

		Documento documento = repositorio.findById(id).orElse(null);
		HttpStatus status = null;

		if(documento == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(documento);
			adicionarLink.adicionarLinkUpdate(documento);
			adicionarLink.adicionarLinkDelete(documento);
			status = HttpStatus.FOUND;
		}

		return new ResponseEntity<Documento>(documento,status);
	}
	
	@PutMapping("/atualizar/{idDocumento}")
	public ResponseEntity<?> atualizarDocumento(@PathVariable Long idDocumento, @RequestBody Documento dados) {
		// Atualiza o documento com os dados fornecidos

		Documento documento = repositorio.findById(idDocumento).orElse(null);
		if(documento == null) {
			return new ResponseEntity<>("Documento não econtrado!", HttpStatus.NOT_FOUND);
		}

		if(dados != null) {
			if(dados.getNumero() != null) {
				documento.setNumero(dados.getNumero());
			}
			if(dados.getTipo() != null) {
				documento.setTipo(dados.getTipo());	
			}
			if(dados.getDataEmissao() != null) {
				documento.setDataEmissao(dados.getDataEmissao());
			}
			repositorio.save(documento);
		}

		return new ResponseEntity<>(documento, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/excluir/{idDocumento}")
	public ResponseEntity<?> excluirDocumento(@PathVariable Long idDocumento){
		// Exclui um documento com base no ID fornecido
		
		Documento verificacao = repositorio.findById(idDocumento).orElse(null);
		
		if(verificacao == null) {
			return new ResponseEntity<>("Documento não econtrado!", HttpStatus.NOT_FOUND);
		}
		
		for(Usuario usuario: repositorioUsuario.findAll()) {
			if(!usuario.getDocumentos().isEmpty()) {
				for(Documento documento: usuario.getDocumentos()) {
					if(documento.getId() == idDocumento) {
						usuario.getDocumentos().remove(documento);
						repositorioUsuario.save(usuario);
					}
					break;
				}
			}
		}
		
		return new ResponseEntity<>("Documento excluido com sucesso!", HttpStatus.ACCEPTED);
		
	}
}
