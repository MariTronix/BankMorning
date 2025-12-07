package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.*;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Service.ContaService;
import Morning.BankMorning.Service.TokenService;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // Import necessário
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // CORREÇÃO: Usando /api/auth para corresponder ao front-end
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ContaService contaService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // DEFINIÇÃO DO DTO DE RESPOSTA (Se não estiver em um arquivo DTO separado)
    public record UsuarioResponse(String message) {}
    public record LoginResponse(String token) {}

    // O método 'cadastro' já está correto com o try-catch
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest) {
        try {
            usuarioService.cadastrarNovoUsuarioeConta(cadastroRequest);
            return ResponseEntity.ok(new UsuarioResponse("Usuário cadastrado com sucesso."));

        } catch (Exception e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            e.printStackTrace();

            if (e.getMessage() != null && (e.getMessage().contains("duplicado") || e.getMessage().contains("Unique"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new UsuarioResponse("Erro: E-mail ou CPF já cadastrado."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UsuarioResponse("Erro ao cadastrar. Verifique os dados fornecidos."));
        }
    }

    // =========================================================================
    // CORREÇÃO: ADIÇÃO DO try-catch NO LOGIN PARA EVITAR O ERRO 500
    // =========================================================================
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest body) {

        try {
            var authenticationToke = new UsernamePasswordAuthenticationToken(
                    body.login(),
                    body.senha()
            );

            // A autenticação lança exceção se as credenciais estiverem erradas
            Authentication authentication = authenticationManager.authenticate(authenticationToke);

            Usuario usuario = (Usuario) authentication.getPrincipal();

            // Graças ao FetchType.EAGER que você implementou, esta linha deve funcionar agora:
            String token = tokenService.gerarToken(usuario.getConta());

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (AuthenticationException e) {
            // Captura falhas de credenciais (BadCredentialsException)
            // O erro é 401 Unauthorized, mas usamos o DTO de mensagem
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UsuarioResponse("Credenciais inválidas. Tente novamente."));
        } catch (Exception e) {
            // Captura NullPointerException (problema com getConta() ou TokenService)
            System.err.println("Erro interno durante o login: " + e.getMessage());
            e.printStackTrace();
            // Retornar 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UsuarioResponse("Ocorreu um erro interno no servidor."));
        }
    }
}