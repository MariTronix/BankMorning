package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // <--- O IMPORTANTE
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponse> depositar(@RequestBody DepositoRequest request) {
        TransacaoResponse response = transacaoService.depositar(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sacar")
    public ResponseEntity<TransacaoResponse> sacar(@RequestBody SaqueRequest request) {
        TransacaoResponse response = transacaoService.sacar(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/transferir")
    public ResponseEntity transferir(@RequestBody TransferenciaRequest request){
        TransacaoResponse response = transacaoService.transferir(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/extrato")
    public ResponseEntity<List<TransacaoResponse>> verExtrato(Authentication authentication) {

        // 1. Pega o usuário que está dentro do Token
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // 2. Pega a conta desse usuário
        Conta contaDoUsuario = usuarioLogado.getConta();

        // 3. Pede o extrato dessa conta
        List<TransacaoResponse> extrato = transacaoService.listarExtrato(contaDoUsuario);

        return ResponseEntity.ok(extrato);
    }
}