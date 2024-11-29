package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioDocumento extends JpaRepository<Documento, Long>{

}
