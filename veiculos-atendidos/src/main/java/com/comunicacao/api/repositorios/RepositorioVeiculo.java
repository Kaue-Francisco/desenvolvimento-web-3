package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioVeiculo extends JpaRepository<Veiculo, Long> {

}
