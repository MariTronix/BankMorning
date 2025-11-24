package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.TransacaoRequest;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping
    public ResponseEntity<Transacao> transferir(@RequestBody @Valid TransacaoRequest request) {
        Transacao novaTransacao = service.realizarTransferencia(request);
        return ResponseEntity.ok(novaTransacao);
    }
}