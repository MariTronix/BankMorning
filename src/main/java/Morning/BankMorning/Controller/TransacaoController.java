package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.TransacaoRequest;
import Morning.BankMorning.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody TransacaoRequest request) {
        transacaoService.depositar(request);
        return ResponseEntity.ok("Depósito realizado com sucesso!");
    }

    @PostMapping("/sacar")
    public ResponseEntity<String> sacar(@RequestBody TransacaoRequest request) {
        transacaoService.sacar(request);
        return ResponseEntity.ok("Saque realizado com sucesso!");
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestBody TransacaoRequest request) {
        transacaoService.transferir(request);
        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }
}