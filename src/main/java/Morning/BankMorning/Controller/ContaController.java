package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/account")
public class ContaController {

    @Autowired
    private ContaService contaService;

    public record BalanceResponse(BigDecimal balance) {}

    // Rota: GET /api/account/balance
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(Principal principal) {

        String cpf = principal.getName(); // Obtém o CPF (username) do JWT

        BigDecimal saldo = contaService.buscarSaldoPorCpf(cpf);

        return ResponseEntity.ok(new BalanceResponse(saldo));
    }

    @GetMapping("/detalhes") // Rota completa: /api/account/detalhes
    public ResponseEntity<ContaResponse> buscarDetalhes(Principal principal) {
        String email = principal.getName();
        ContaResponse response = contaService.buscarContaPorEmailUsuario(email);
        return ResponseEntity.ok(response);
    }
}