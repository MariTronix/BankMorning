package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Importação necessária

@RestController
@RequestMapping("/api/usuarios") // Altere o RequestMapping para /api/usuarios se for o padrão
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Rota: GET /api/usuarios/profile
    // Esta rota é o fallback que o frontend está buscando para obter o nome.
    @GetMapping("/profile")
    public ResponseEntity<UsuarioResponse> getProfile(Principal principal) {
        // Obtém o identificador (E-mail ou CPF) do JWT
        String identificador = principal.getName();

        // Chama o Service para buscar o perfil (que deve ter o @Transactional para resolver Lazy Loading)
        UsuarioResponse response = service.buscarPerfilPorEmail(identificador);

        return ResponseEntity.ok(response);
    }

    // NOTA: Os caminhos de rotas abaixo são relativos a /api/usuarios

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Integer id, @RequestBody @Valid UsuarioRequest request) {
        UsuarioResponse response = service.atualizarUsuario(id, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> deletar(@PathVariable @Valid Integer id) {
        UsuarioResponse response = service.deletarUsuario(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cadastrar") // A URL final seria /api/usuarios/cadastrar
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid CadastroRequest request) {
        // Você precisa ter esse método criarUsuario no seu Service
        UsuarioResponse novoUsuario = service.cadastrarNovoUsuarioeConta(request);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

}