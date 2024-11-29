package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioTelefone extends JpaRepository<Telefone, Long> {

}