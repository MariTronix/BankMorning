package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Dto.LoginRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Repository.UsuarioRepository;
import Morning.BankMorning.Service.ContaService;
import Morning.BankMorning.Service.TokenService;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
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
    private UsuarioService usuarioService;

    @Autowired
    private ContaService contaService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest) {

        UsuarioResponse response = usuarioService.cadastrarNovoUsuarioeConta(cadastroRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest body) {

        ContaResponse response = contaService.login(body);

        return ResponseEntity.ok(response);
    }
}