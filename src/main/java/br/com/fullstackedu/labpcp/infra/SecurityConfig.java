package br.com.fullstackedu.labpcp.infra;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${jwt.public.key}")
    RSAPublicKey key;

    @Value("${jwt.private.key}")
    RSAPrivateKey priv;

    @Bean // feijão mágico
    // criação de uma classe
    // classe que representa os filtros do Spring Security
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // permite os endpoints que tenham o texto que condiz com /login
                        .requestMatchers(HttpMethod.POST, "/cadastro").permitAll()
                        .anyRequest().authenticated() // pede autenticação para todos os endpoints que não foram permitidos
                )
                .csrf(AbstractHttpConfigurer::disable) // desabilita o CSRF, ele bloqueia alguns tipos de chamadas por padrão
                .oauth2ResourceServer(oath2 ->
                        oath2.jwt(Customizer.withDefaults()) // configurar o JWT com os padrões de projeto -> os beans JwtDecoder e JwtEncoder
                ) // configurar esse programa como um Servidor de Recursos OAuth2
                // filtro adicionado a cadeia de filtros do Spring Security

                .sessionManagement(session ->  // sessão é uma forma de manter um usuário logado
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // indica que o sistema nunca fica logado, ele depende do token para validar um acesso
                )
        ;

        return http.build(); // Gera um tipo para o SecurityFilterChain, ele preenche a Chain
    }

    @Bean // Configuração / Criação de uma Classe de configuração que o Spring Security irá usar
    JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder
                .withPublicKey(this.key)
                .build(); //Crie uma classe decodificador que usa a chave publica
    }

    @Bean // Configuração / Criação de uma Classe de configuração que o Spring Security irá usar
    JwtEncoder jwtEncoder(){

        JWK jwk = new RSAKey.Builder(this.key)
                .privateKey(this.priv)
                .build(); // crie um objeto JWK com o campo privateKey
        // JWK controla chaves de encriptação

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>( // lista imutável de JWK
                new JWKSet(jwk)
        );

        return new NimbusJwtEncoder(jwks); // Criar um encriptador a partir do JWK criado anteriormente
    }

    @Bean // Configuração para as senhas
    // Vai ser usado para encriptar as senhas antes de salvá-las no banco de dados
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(); // codifica, ou criptografar, senhas com o software BCrypt
    }
}
