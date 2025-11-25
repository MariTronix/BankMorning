package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.LoginRequest;
import Morning.BankMorning.Dto.LoginResponse;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity login(@RequestBody LoginRequest body) {
        // 1. Busca usuário pelo email
        Usuario usuario = repository.findByLogin(body.login())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Compara a senha enviada com o hash do banco
        if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
            // 3. Gera e devolve o token
            String token = tokenService.gerarToken(usuario);
            return ResponseEntity.ok(new LoginResponse(token));
        }

        // 4. Senha errada
        return ResponseEntity.badRequest().build();
    }
}