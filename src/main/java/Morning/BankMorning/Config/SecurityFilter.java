package Morning.BankMorning.Config;

import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Recupera o Token da requisição
        var token = recuperarToken(request);

        if (token != null) {
            try {
                // 2. Extrai o Subject (CPF) do token. O método getSubject devolve uma String.
                var subject = tokenService.getSubject(token);

                // 3. Verifica se o subject (CPF) é válido e não está vazio
                if (subject != null && !subject.isEmpty()) {

                    // 4. Busca o usuário no banco usando o CPF (findByCpf), pois é o que está no Subject do seu token
                    var usuario = repository.findByCpf(subject).orElse(null);

                    if (usuario != null) {
                        // 5. Se o usuário existir, força a autenticação no contexto do Spring Security
                        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Logar o erro se a validação do token falhar, mas permite o fluxo continuar
                // Para não travar a aplicação, em caso de token expirado ou inválido
                System.err.println("Erro ao processar token: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}