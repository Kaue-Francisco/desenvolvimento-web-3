package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Usuario findByNome(String nome);
}