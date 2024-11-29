package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.CredencialUsuarioSenha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCredencialUsuarioSenha extends JpaRepository<CredencialUsuarioSenha, Long>{

}
