package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Endereco;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnderecoSelecionador {
    public Endereco selecionar(List<Endereco> enderecos, long id) {
        Endereco selecionado = null;

        for (Endereco endereco: enderecos) {
            if (endereco.getId() == id) {
                selecionado = endereco;
            }
        }
        return selecionado;
    }
}
