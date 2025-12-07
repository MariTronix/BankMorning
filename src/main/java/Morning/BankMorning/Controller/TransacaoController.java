package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    // MÉTODO AUXILIAR (DEVE ESTAR NO CONTROLLER!)
    private Conta getContaDoUsuario(Authentication authentication) {
        // Assume que o principal é o objeto Usuario que tem o campo Conta
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        return usuarioLogado.getConta();
    }


    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponse> depositar(
            Authentication authentication,
            @RequestBody @Valid DepositoRequest request) {

        Conta contaOrigem = getContaDoUsuario(authentication);
        TransacaoResponse response = transacaoService.depositar(contaOrigem, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // CORREÇÃO: ADICIONANDO SAQUE E TRANSFERÊNCIA
    // ------------------------------------------------------------------

    @PostMapping("/sacar")
    public ResponseEntity<TransacaoResponse> sacar(
            Authentication authentication,
            @RequestBody @Valid SaqueRequest request) {

        Conta contaOrigem = getContaDoUsuario(authentication);
        TransacaoResponse response = transacaoService.sacar(contaOrigem, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/transferir") // <--- MÉTODO QUE ESTAVA FALTANDO!
    public ResponseEntity<TransacaoResponse> transferir(
            Authentication authentication,
            @RequestBody @Valid TransferenciaRequest request){

        Conta contaOrigem = getContaDoUsuario(authentication);
        TransacaoResponse response = transacaoService.transferir(contaOrigem, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ------------------------------------------------------------------

    @GetMapping("/extrato")
    public ResponseEntity<List<TransacaoResponse>> verExtrato(Authentication authentication) {
        // Usa o método auxiliar
        Conta contaDoUsuario = getContaDoUsuario(authentication);

        // Chama o Service
        List<TransacaoResponse> extrato = transacaoService.listarExtrato(contaDoUsuario);

        return ResponseEntity.ok(extrato);
    }
}