package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.CriarContaRequest;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<Conta> criarConta(@RequestBody @Valid CriarContaRequest request) {
        Conta novaConta = contaService.criarConta(request);
        return ResponseEntity.ok(novaConta);
    }
}