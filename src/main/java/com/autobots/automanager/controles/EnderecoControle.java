package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoSelecionador;
import com.autobots.automanager.modelo.AdicionadorLinkEndereco;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {

    @Autowired
    private EnderecoRepositorio repositorio;
    @Autowired
    private EnderecoSelecionador selecionador;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private AdicionadorLinkEndereco adicionadorLink;

    @GetMapping("/enderecos")
    public ResponseEntity<List<Endereco>> obterEnderecos() {
        List<Endereco> enderecos = repositorio.findAll();
        if (enderecos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(enderecos);
            return new ResponseEntity<>(enderecos, HttpStatus.FOUND);
        }
    }

    @GetMapping("/endereco/{id}")
    public Endereco obterEndereco(@PathVariable long id) {
        List<Endereco> enderecos = repositorio.findAll();
        if (enderecos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(enderecos);
            return selecionador.selecionar(enderecos, id);
        }
    }

    @PostMapping("/cadastrar/{id_user}")
    public Endereco cadastrarEndereco(@PathVariable("id_user") long id_user, @RequestBody Endereco novoEndereco) {

        Optional<Cliente> usuarioOptional = clienteRepositorio.findById(id_user);

        if (usuarioOptional.isPresent()) {
            Cliente usuario = usuarioOptional.get();

            // Verificar se o cliente já tem um endereço
            if (usuario.getEndereco() != null) {
                // Deletar o endereço antigo (se necessário)
                Endereco enderecoAntigo = usuario.getEndereco();
                repositorio.delete(enderecoAntigo); // Repositório de endereço deve ser chamado para deletar

                // Substituir o endereço antigo pelo novo
                usuario.setEndereco(novoEndereco);
            } else {
                // Adicionar o novo endereço ao cliente
                usuario.setEndereco(novoEndereco);
            }

            // Salvar o cliente com o novo endereço
            clienteRepositorio.save(usuario);

            return novoEndereco;  // Retornar o endereço cadastrado
        } else {
            // Lidar com o caso de usuário não encontrado
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    // Método para atualizar o endereço de um cliente
    @PutMapping("/atualizar/{id_user}")
    public ResponseEntity<?> atualizarEndereco(
            @PathVariable("id_user") long id_user,
            @RequestBody Endereco dadosAtualizados) {

        Optional<Cliente> usuarioOptional = clienteRepositorio.findById(id_user);

        if (usuarioOptional.isPresent()) {
            Cliente usuario = usuarioOptional.get();

            // Verificar se o cliente possui um endereço para atualizar
            if (usuario.getEndereco() != null) {
                Endereco enderecoAtual = usuario.getEndereco();

                // Atualizar os dados do endereço
                enderecoAtual.setRua(dadosAtualizados.getRua());
                enderecoAtual.setNumero(dadosAtualizados.getNumero());
                enderecoAtual.setBairro(dadosAtualizados.getBairro());
                enderecoAtual.setCidade(dadosAtualizados.getCidade());
                enderecoAtual.setEstado(dadosAtualizados.getEstado());
                enderecoAtual.setCodigoPostal(dadosAtualizados.getCodigoPostal());

                // Salvar o endereço atualizado
                repositorio.save(enderecoAtual);

                return new ResponseEntity<>(enderecoAtual, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("O cliente não possui endereço.", HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    @DeleteMapping("/deletar/{id_user}")
    public ResponseEntity<?> deletarEndereco(@PathVariable("id_user") long id_user) {
        Optional<Cliente> usuarioOptional = clienteRepositorio.findById(id_user);

        if (usuarioOptional.isPresent()) {
            Cliente usuario = usuarioOptional.get();

            // Verificar se o cliente possui um endereço para deletar
            if (usuario.getEndereco() != null) {
                Endereco enderecoAntigo = usuario.getEndereco();

                // Remover o endereço do cliente
                usuario.setEndereco(null);
                clienteRepositorio.save(usuario);

                // Deletar o endereço da base de dados
                repositorio.delete(enderecoAntigo);

                return new ResponseEntity<>("Endereço deletado com sucesso.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("O cliente não possui endereço.", HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }
}