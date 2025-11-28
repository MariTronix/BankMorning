package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.*;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.ContaService;
import Morning.BankMorning.Service.TokenService;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
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

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest) {

        UsuarioResponse response = usuarioService.cadastrarNovoUsuarioeConta(cadastroRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest body) {

        var authenticationToke = new UsernamePasswordAuthenticationToken(
                body.login(),
                body.senha()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToke);

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = tokenService.gerarToken(usuario.getConta());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}