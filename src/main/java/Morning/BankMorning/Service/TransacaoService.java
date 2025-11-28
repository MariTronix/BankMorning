package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.TransacaoRequest;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Transactional
    private Transacao salvarTransacao(TransacaoRequest transacaoRequest, TipoDeTransacao tipoDeTransacao) {
        Transacao transacao = new Transacao();
        Conta contaOrigem = transacao.getContaOrigem();
        Conta contaDestino = transacao.getContaDestino();

        if (contaOrigem != null) {
            contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new IllegalArgumentException("Cpf de Origem Inválido"));
        }

        if (contaDestino != null) {
            contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new IllegalArgumentException());
        }

        transacao.setValor(transacaoRequest.valor());
        transacao.setTipo(tipoDeTransacao);
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao depositar(TransacaoRequest transacaoRequest) {

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }

        Conta contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));


        BigDecimal novoSaldo = contaDestino.getSaldo().add(transacaoRequest.valor());
        contaDestino.setSaldo(novoSaldo);
        contaRepository.save(contaDestino);

        return salvarTransacao(transacaoRequest, TipoDeTransacao.DEPOSITO);
    }

    @Transactional
    public Transacao sacar(TransacaoRequest transacaoRequest) {
        Conta contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }

        if (contaOrigem.getSaldo().compareTo(transacaoRequest.valor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        BigDecimal novoSaldo = contaOrigem.getSaldo().subtract(transacaoRequest.valor());
        contaOrigem.setSaldo(novoSaldo);
        contaRepository.save(contaOrigem);

        return salvarTransacao(transacaoRequest, TipoDeTransacao.SAQUE);
    }

    @Transactional
    public Transacao transferir(TransacaoRequest transacaoRequest) {

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }

        Conta contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));

        Conta contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));

        BigDecimal valor = transacaoRequest.valor();

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência.");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaRepository.save(contaOrigem);

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepository.save(contaDestino);

        return salvarTransacao(transacaoRequest, TipoDeTransacao.TRANSFERENCIA);
    }
}