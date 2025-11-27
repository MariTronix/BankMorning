package Morning.BankMorning.Service;

import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Random;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public void criarContaParaUsuario(Usuario usuario) {
        Conta novaConta = new Conta();
        novaConta.setUsuario(usuario);
        novaConta.setSaldo(BigDecimal.ZERO);
        novaConta.setAgencia(1);
        novaConta.setNumeroConta(gerarNumeroContaUnico());

        contaRepository.save(novaConta);
    }

    private Integer gerarNumeroContaUnico() {
        return 100000 + new Random().nextInt(900000);
    }
}