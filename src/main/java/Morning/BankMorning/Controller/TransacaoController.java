package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Service.TransacaoService;
import Morning.BankMorning.Service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaService contaService;

    // Metodo auxiliar : Busca a Conta usando o identificador do Principal
    private Conta getContaDoUsuario(Principal principal) {
        String identificador = principal.getName();
        // CHAMA O NOVO MÉTODO no Service. Retorna a Conta ATTACHED.
        Conta conta = contaService.buscarContaModelPorEmailUsuario(identificador);
        return conta;
    }

    // --- DEPÓSITO ---
    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponse> depositar(
            Principal principal,
            @RequestBody @Valid DepositoRequest request) {

        TransacaoResponse response = transacaoService.depositar( request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- SAQUE ---
    @PostMapping("/sacar")
    public ResponseEntity<TransacaoResponse> sacar(
            Principal principal,
            @RequestBody @Valid SaqueRequest request) {

        Conta contaOrigem = getContaDoUsuario(principal);
        TransacaoResponse response = transacaoService.sacar(contaOrigem, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- TRANSFERÊNCIA ---
    @PostMapping("/transferir")
    public ResponseEntity<TransacaoResponse> transferir(
            Principal principal,
            @RequestBody @Valid TransferenciaRequest request){

        Conta contaOrigem = getContaDoUsuario(principal);
        TransacaoResponse response = transacaoService.transferir(contaOrigem, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- EXTRATO ---
    @GetMapping("/extrato")
    public ResponseEntity<List<TransacaoResponse>> verExtrato(Principal principal) {
        // Usa o método auxiliar
        Conta contaDoUsuario = getContaDoUsuario(principal);

        List<TransacaoResponse> extrato = transacaoService.listarExtrato(contaDoUsuario);

        return ResponseEntity.ok(extrato);
    }
}
