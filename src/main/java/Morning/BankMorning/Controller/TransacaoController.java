package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.TransacaoRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponse> depositar(@RequestBody TransacaoRequest request) {
        TransacaoResponse response = transacaoService.depositar(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sacar")
    public ResponseEntity<TransacaoResponse> sacar(@RequestBody TransacaoRequest request) {
        TransacaoResponse response = transacaoService.sacar(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/transferir")
    public ResponseEntity<TransacaoResponse> transferir(@RequestBody TransacaoRequest request) {
        TransacaoResponse response = transacaoService.transferir(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}