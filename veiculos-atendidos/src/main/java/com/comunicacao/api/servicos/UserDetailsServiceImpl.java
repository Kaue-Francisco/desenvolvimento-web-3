package com.comunicacao.api.servicos;

import com.comunicacao.api.adaptadores.UserDetailsImpl;
import com.comunicacao.api.entidades.Credencial;
import com.comunicacao.api.entidades.CredencialUsuarioSenha;
import com.comunicacao.api.entidades.Usuario;
import com.comunicacao.api.repositorios.RepositorioCredencialUsuarioSenha;
import com.comunicacao.api.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RepositorioUsuario repositorio;

    @Autowired
    private RepositorioCredencialUsuarioSenha repositorioCredencialUsuarioSenha;

    public Usuario selecionar(List<Usuario> objetos, String identificador) {
        Usuario usuario = null;
        for (Usuario objeto : objetos) {
            Set<Credencial> credencial = objeto.getCredenciais();
            for (Credencial credencia : credencial) {
                for (CredencialUsuarioSenha cred : repositorioCredencialUsuarioSenha.findAll()) {
                    if (credencia.getId() == cred.getId()) {
                        String nomeUsuario = cred.getNomeUsuario();
                        if (nomeUsuario.trim().equals(identificador.trim())) {
                            usuario = objeto;
                            break;
                        }
                    }
                }
            }
        }
        return usuario;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Usuario> usuarios = repositorio.findAll();
        Usuario usuario = selecionar(usuarios, username);
        if (usuario == null) {
            throw new UsernameNotFoundException(username);
        }
        
        String nomeUsuario = "";
        String password = "";
        for (Credencial credencia : usuario.getCredenciais()) {
            for (CredencialUsuarioSenha cred : repositorioCredencialUsuarioSenha.findAll()) {
                if (credencia.getId() == cred.getId()) {
                    nomeUsuario = cred.getNomeUsuario();
                    password = cred.getSenha();
                }
            }
        }
        List<SimpleGrantedAuthority> authorities = usuario.getNivelDeAcesso().stream()
                .map(nivel -> new SimpleGrantedAuthority("ROLE_" + nivel.name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(nomeUsuario, password, authorities);
    }
}