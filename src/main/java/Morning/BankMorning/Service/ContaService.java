package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.CriarContaRequest;
import Morning.BankMorning.Model.Cliente;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Repository.ClienteRepository;
import Morning.BankMorning.Repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Conta criarConta(CriarContaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + request.getIdCliente()));

        Conta novaConta = new Conta();
        novaConta.setCliente(cliente);
        novaConta.setAgencia("0001");
        novaConta.setNumeroConta(gerarNumeroContaUnico());

        if (request.getSaldoInicial() != null) {
            novaConta.setSaldo(request.getSaldoInicial());
        } else {
            novaConta.setSaldo(BigDecimal.ZERO);
        }

        return contaRepository.save(novaConta);
    }

    private String gerarNumeroContaUnico() {
        String numeroGerado;
        do {
            int randomNum = new Random().nextInt(900000) + 100000;
            numeroGerado = String.valueOf(randomNum);


        } while (contaRepository.existsByNumeroConta(numeroGerado));

        return numeroGerado;
    }
}