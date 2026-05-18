package cl.duoc.libroDigital.authService.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cl.duoc.libroDigital.authService.util.JwtUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @SuppressWarnings("null")
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        //Ignorar endpoints públicos
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        //Validar header correctamente
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                var claims = jwtUtil.extractAllClaims(token);

                String username = claims.getSubject();
                String rolesStr = (String) claims.get("roles");

                //Manejo seguro de roles
                List<SimpleGrantedAuthority> authorities = rolesStr != null
                        ? Arrays.stream(rolesStr.split(","))
                            .filter(r -> !r.isBlank())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
                        : List.of();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                //Evitar sobrescribir autenticación existente
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (Exception e) {
                //Token inválido → limpiar contexto
                SecurityContextHolder.clearContext();
            }
        }

        //continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}