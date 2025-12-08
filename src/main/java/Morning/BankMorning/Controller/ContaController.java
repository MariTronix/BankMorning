package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/account")
public class ContaController {

    @Autowired
    private ContaService contaService;

    // Usando o mesmo record para clareza no endpoint de saldo
    public record BalanceResponse(BigDecimal saldo) {}

    // Dentro de ContaController.java
    @GetMapping("/id/{id}") // Rota: /api/account/id/{id}
    public ResponseEntity<ContaResponse> getContaPorId(@PathVariable Integer id) {
        ContaResponse response = contaService.buscarContaPorId(id);
        return ResponseEntity.ok(response);
    }

    // Rota: GET /api/account/balance
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(Principal principal) {

        // Obtém o identificador (E-mail ou CPF) do JWT
        String identificador = principal.getName();

        // Chamamos o serviço que busca o saldo pelo identificador.
        // Este método no Service já deve ter o @Transactional(readOnly = true).
        BigDecimal saldo = contaService.buscarSaldoPorCpf(identificador);

        return ResponseEntity.ok(new BalanceResponse(saldo));
    }

    @GetMapping("/detalhes") // Rota completa: /api/account/detalhes
    public ResponseEntity<ContaResponse> buscarDetalhes(Principal principal) {

        // Obtém o identificador (E-mail ou CPF) do JWT
        String identificador = principal.getName();

        // Esta chamada utiliza o ContaService que, com o @Transactional(readOnly = true),
        // resolve o Lazy Loading para carregar o nome do usuário.
        ContaResponse response = contaService.buscarContaPorEmailUsuario(identificador);

        return ResponseEntity.ok(response);
    }
}