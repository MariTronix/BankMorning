package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.ClienteRequest;
import Morning.BankMorning.Dto.ClienteResponse;
import Morning.BankMorning.Service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteRequest request) {
        ClienteResponse response = service.criarCliente(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
