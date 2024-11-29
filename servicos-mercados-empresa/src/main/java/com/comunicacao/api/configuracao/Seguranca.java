package com.comunicacao.api.configuracao;

import com.comunicacao.api.filtros.Autenticador;
import com.comunicacao.api.filtros.Autorizador;
import com.comunicacao.api.jwt.ProvedorJwt;
import com.comunicacao.api.servicos.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Seguranca extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl servico;

    @Autowired
    private ProvedorJwt provedorJwt;

    private static final String[] rotasPublicas = { "/autenticacao/login", "/usuario/cadastrar-cliente", "/usuario/listar/" };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests()
                .anyRequest().permitAll(); // Permitir todas as requisições

        // Remover os filtros de autenticação e autorização
        // http.addFilter(new Autenticador(authenticationManager(), provedorJwt));
        // http.addFilter(new Autorizador(authenticationManager(), provedorJwt, servico));

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder autenticador) throws Exception {
        autenticador.userDetailsService(servico).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    // Expondo o AuthenticationManager como um bean
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return fonte;
    }
}