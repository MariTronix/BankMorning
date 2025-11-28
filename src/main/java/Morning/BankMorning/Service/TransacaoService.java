package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Dto.TransacaoRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private static ContaService contaService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private static Transacao converterParaModel(TransacaoRequest request) {
        if (request == null) {
            return null;
        }

        Transacao transacao = new Transacao();
        BeanUtils.copyProperties(request, transacao);

        return transacao;
    }

    private static TransacaoResponse converterParaResponse(Transacao transacao) {
        if (transacao == null) {
            return null;
        }
        String cpfContaOrigem = null;
        String cpfContaDestino = null;

        if (transacao.getContaOrigem() != null) {
            cpfContaOrigem = transacao.getContaOrigem().getUsuario().getCpf();
        }

        if (transacao.getContaDestino() != null) {
            cpfContaDestino = transacao.getContaDestino().getUsuario().getCpf();
        }


        return new TransacaoResponse(cpfContaOrigem, cpfContaDestino, transacao.getValor(), transacao.getTipo());
    }

    @Transactional
    private TransacaoResponse salvarTransacao(TransacaoRequest transacaoRequest, TipoDeTransacao tipoDeTransacao) {
        Transacao transacao = new Transacao();
        Conta contaOrigem = null;
        Conta contaDestino = null;

        if (transacaoRequest.cpfContaOrigem() != null) {
            contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta com CPF " + transacaoRequest.cpfContaOrigem() + "não encontrada."));
        }

        if (transacaoRequest.cpfContaDestino() != null) {
            contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta com CPF " + transacaoRequest.cpfContaDestino() + "não encontrada."));
        }

        transacao.setValor(transacaoRequest.valor());
        transacao.setTipo(tipoDeTransacao);
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);

        Transacao transacaoSalva = transacaoRepository.save(transacao);

        return converterParaResponse(transacaoSalva);
    }

    @Transactional
    public TransacaoResponse depositar(TransacaoRequest transacaoRequest) {

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do depósito deve ser maior que zero.");
        }

        Conta contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada."));

        BigDecimal novoSaldo = contaDestino.getSaldo().add(transacaoRequest.valor());
        contaDestino.setSaldo(novoSaldo);
        contaRepository.save(contaDestino);


        return salvarTransacao(transacaoRequest, TipoDeTransacao.DEPOSITO);
    }

    @Transactional
    public TransacaoResponse sacar(TransacaoRequest transacaoRequest) {
        Conta contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta com CPF " + transacaoRequest.cpfContaOrigem() + "não encontrada."));

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do saque deve ser positivo.");
        }

        if (contaOrigem.getSaldo().compareTo(transacaoRequest.valor()) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }

        BigDecimal novoSaldo = contaOrigem.getSaldo().subtract(transacaoRequest.valor());
        contaOrigem.setSaldo(novoSaldo);
        contaRepository.save(contaOrigem);

        return salvarTransacao(transacaoRequest, TipoDeTransacao.SAQUE);
    }

    @Transactional
    public TransacaoResponse transferir(TransacaoRequest transacaoRequest) {

        if (transacaoRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor da transferência deve ser positivo.");
        }

        Conta contaOrigem = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaOrigem()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta com CPF " + transacaoRequest.cpfContaOrigem() + "não encontrada."));

        Conta contaDestino = contaRepository.findByUsuario_Cpf(transacaoRequest.cpfContaDestino()).orElseThrow(() -> new RecursoNaoEncontradoException("Conta com CPF " + transacaoRequest.cpfContaDestino() + " não encontrada."));

        BigDecimal valor = transacaoRequest.valor();

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaRepository.save(contaOrigem);

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepository.save(contaDestino);

        return salvarTransacao(transacaoRequest, TipoDeTransacao.TRANSFERENCIA);
    }
}