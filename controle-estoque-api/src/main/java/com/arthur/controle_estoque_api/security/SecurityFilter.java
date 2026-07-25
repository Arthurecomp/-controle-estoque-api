package com.arthur.controle_estoque_api.security;

import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;


    public SecurityFilter(TokenService tokenService,
                          UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        var token = recuperarToken(request);


        if(token != null){

            String email = tokenService.validarToken(token);


            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElse(null);


            if(usuario != null){

                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                usuario.getAuthorities()
                        );


                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }


        filterChain.doFilter(request, response);
    }


    private String recuperarToken(HttpServletRequest request){

        var authHeader = request.getHeader("Authorization");

        if(authHeader == null){
            return null;
        }

        return authHeader.replace("Bearer ", "");
    }
}