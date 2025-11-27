package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody DepositoRequest request) {
        service.depositar(request.getIdConta(), request.getValor());
        return ResponseEntity.ok("Depósito realizado com sucesso!");
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestBody TransferenciaRequest request) {
        service.transferir(request.getIdContaOrigem(), request.getIdContaDestino(), request.getValor());
        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }
}