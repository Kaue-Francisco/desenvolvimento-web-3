package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioVenda extends JpaRepository<Venda, Long> {

}
