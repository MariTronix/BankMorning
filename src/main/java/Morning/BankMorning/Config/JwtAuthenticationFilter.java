package Morning.BankMorning.Config;

import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        // 1. Se não tiver Authorization ou não começar com Bearer, pula o filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extrai o token
        String token = authHeader.substring(7);

        // 3. Valida token
        if (tokenService.validarToken(token)) {

            String email = tokenService.getSubject(token);

            var usuarioOpt = usuarioRepository.findByEmail(email);

            if (usuarioOpt.isPresent()) {

                var usuario = usuarioOpt.get();

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                var authentication =
                        new UsernamePasswordAuthenticationToken(usuario, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. Continua na cadeia normal
        filterChain.doFilter(request, response);
    }
}
