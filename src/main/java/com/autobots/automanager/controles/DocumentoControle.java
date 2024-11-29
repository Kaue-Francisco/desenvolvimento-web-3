package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.modelo.DocumentoSelecionador;
import com.autobots.automanager.modelo.AdicionadorLinkDocumento;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {
    @Autowired
    private DocumentoRepositorio repositorio;
    @Autowired
    private DocumentoSelecionador selecionador;
    @Autowired
    private DocumentoAtualizador atualizador;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private AdicionadorLinkDocumento adicionadorLink;

    @GetMapping("/documentos")
    public ResponseEntity<List<Documento>> obterDocumentos() {
        List<Documento> documentos = repositorio.findAll();
        if (documentos.isEmpty()) {
            ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return resposta;
        } else {
            adicionadorLink.adicionarLink(documentos);
            ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(documentos, HttpStatus.FOUND);
            return resposta;
        }
    }

    @GetMapping("/documento/{id}")
    public Documento obterDocumento(@PathVariable long id) {
        List<Documento> documentos = repositorio.findAll();
        if (documentos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(documentos);
            return selecionador.selecionar(documentos, id);
        }
    }

    @PostMapping("/cadastrar/{id_user}")
    public Documento cadastrarDocumento(@PathVariable("id_user") long id_user, @RequestBody Documento documento) {
        // Buscar o usuário pelo ID
        Optional<Cliente> usuarioOptional = clienteRepositorio.findById(id_user);

        // Verificar se o usuário existe
        if (usuarioOptional.isPresent()) {
            Cliente usuario = usuarioOptional.get();

            // Adicionar o documento ao usuário
            usuario.getDocumentos().add(documento);

            // Salvar o cliente com o novo documento
            clienteRepositorio.save(usuario);

            return documento;  // Retornar o documento cadastrado
        } else {
            // Lidar com o caso de usuário não encontrado
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    @PutMapping("/atualizar/{id_documento}")
    public ResponseEntity<?> atualizarDocumento(
            @PathVariable("id_documento") long idDocumento,
            @RequestBody Documento dadosAtualizados) {

        // Verificar se o documento existe
        Optional<Documento> documentoOptional = repositorio.findById(idDocumento);

        if (documentoOptional.isPresent()) {
            Documento documento = documentoOptional.get();

            // Atualizar os dados do documento
            atualizador.atualizar(documento, dadosAtualizados);

            // Salvar o documento atualizado no repositório
            repositorio.save(documento);

            return new ResponseEntity<>(documento, HttpStatus.OK);  // Retorna o documento atualizado
        } else {
            // Lidar com o caso de documento não encontrado
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento não encontrado");
        }
    }

    @DeleteMapping("/deletar/{id_documento}")
    public ResponseEntity<?> deletarDocumento(@PathVariable("id_documento") long idDocumento) {
        // Buscar o documento pelo ID
        Optional<Documento> documentoOptional = repositorio.findById(idDocumento);

        if (documentoOptional.isPresent()) {
            Documento documento = documentoOptional.get();

            // Verificar se o documento está associado a algum cliente
            Optional<Cliente> clienteOptional = clienteRepositorio.findAll().stream()
                    .filter(cliente -> cliente.getDocumentos().contains(documento))
                    .findFirst();

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();

                // Remover o documento da lista de documentos do cliente
                cliente.getDocumentos().remove(documento);

                // Salvar o cliente atualizado (sem o documento)
                clienteRepositorio.save(cliente);

                // Deletar o documento do repositório
                repositorio.delete(documento);

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Retornar status 204 No Content
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento não está associado a nenhum cliente");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento não encontrado");
        }
    }
}