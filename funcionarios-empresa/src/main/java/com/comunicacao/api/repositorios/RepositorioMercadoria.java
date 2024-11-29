package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Mercadoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioMercadoria  extends JpaRepository<Mercadoria, Long>{

}
