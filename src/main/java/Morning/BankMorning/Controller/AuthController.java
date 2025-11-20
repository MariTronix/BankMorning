package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.LoginRequest;
import Morning.BankMorning.Dto.LoginResponse;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        // 1. Buscar o usuário pelo login
        Usuario usuario = repository.findByLogin(request.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos")); // Mensagem genérica por segurança

        // 2. Verificar se a senha bate (Senha pura vs Hash no banco)
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        // 3. Se passou, gera o token
        String token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}