package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.modelo.TelefoneSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {

    @Autowired
    private TelefoneRepositorio repositorio;
    @Autowired
    private TelefoneSelecionador selecionador;
    @Autowired
    private TelefoneAtualizador atualizador;
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @GetMapping("/telefones")
    public List<Telefone> obterTelefones() {
        List<Telefone> telefones = repositorio.findAll();
        return telefones;
    }

    @GetMapping("/telefone/{id}")
    public Telefone obterTelefone(@PathVariable long id) {
        List<Telefone> telefones = repositorio.findAll();
        return selecionador.selecionar(telefones, id);
    }

    @PostMapping("/cadastrar/{id_user}")
    public Telefone cadastrarTelefone(@PathVariable("id_user") long id_user, @RequestBody Telefone telefone) {

        Optional<Cliente> usuarioOptional = clienteRepositorio.findById(id_user);

        if (usuarioOptional.isPresent()) {
            Cliente usuario = usuarioOptional.get();

            // Adicionar o telefone ao usuário
            usuario.getTelefones().add(telefone);

            clienteRepositorio.save(usuario);

            return telefone; // Retornar o telefone cadastrado.
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }
    }

    @PutMapping("/atualizar/{id_telefone}")
    public ResponseEntity<?> atualizarTelefone(
            @PathVariable("id_telefone") long idTelefone,
            @RequestBody Telefone dadosAtualizados) {

                Optional<Telefone> telefoneOptional = repositorio.findById(idTelefone);

                if (telefoneOptional.isPresent()) {
                    Telefone telefone = telefoneOptional.get();

                    // Atualizar os dados do telefone
                    atualizador.atualizar(telefone, dadosAtualizados);

                    repositorio.save(telefone);

                    return new ResponseEntity<>(telefone, HttpStatus.OK);
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Telefone não encontrado!");
                }
    }

    @DeleteMapping("/deletar/{id_telefone}")
    public ResponseEntity<?> deletarTelefone(@PathVariable("id_telefone") long idTelefone) {

        Optional<Telefone> telefoneOptional = repositorio.findById(idTelefone);

        if (telefoneOptional.isPresent()) {
            Telefone telefone = telefoneOptional.get();

            Optional<Cliente> clienteOptional = clienteRepositorio.findAll().stream()
                    .filter(cliente -> cliente.getTelefones().contains(telefone))
                    .findFirst();

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();

                cliente.getTelefones().remove(telefone);

                clienteRepositorio.save(cliente);

                repositorio.delete(telefone);

                return ResponseEntity.ok().build(); // Retorna 200 OK em caso de sucesso
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Telefone não encontrado.");
        }
    }
}
