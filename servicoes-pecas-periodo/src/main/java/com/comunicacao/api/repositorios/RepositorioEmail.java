package com.comunicacao.api.repositorios;

import com.comunicacao.api.entidades.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioEmail extends JpaRepository<Email, Long>{

}
