package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.*;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Service.TokenService;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Mantendo compatibilidade com seus testes
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest) {
        // Não precisa de try-catch aqui, o @ExceptionHandler cuida disso
        UsuarioResponse response = usuarioService.cadastrarNovoUsuarioeConta(cadastroRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest body) {
        // O authenticationManager lança BadCredentialsException se a senha for errada
        // Essa exceção será capturada pelo @ExceptionHandler(BadCredentialsException.class) lá embaixo
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                body.login(),
                body.senha()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenService.gerarToken(usuario.getConta());

        return ResponseEntity.ok(new LoginResponse(token));
    }


    // 1. Captura erros de validação (@Valid) -> Retorna 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // 2. Captura Regras de Negócio (ex: CPF duplicado) -> Retorna 400
    @ExceptionHandler(ArgumentoInvalidoException.class)
    public ResponseEntity<String> handleArgumentoInvalido(ArgumentoInvalidoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // 3. Captura Senha Incorreta -> Retorna 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

    // 4. Captura Erros Gerais -> Retorna 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
    }
}