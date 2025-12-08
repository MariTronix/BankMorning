package Morning.BankMorning.Controller;

import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import  java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contas")
public class ContaController {
    @Autowired
    private ContaRepository contaRepository;

    @GetMapping
    public List<Conta> listarContas(){
        return contaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> buscarContaPorId(@PathVariable Integer id){
        Optional<Conta> conta = contaRepository.findById(id);
//
        if (conta.isPresent()){
            return ResponseEntity.ok(conta.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
