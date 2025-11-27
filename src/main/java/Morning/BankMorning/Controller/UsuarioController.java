package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest request) {
        UsuarioResponse response = service.criarUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
