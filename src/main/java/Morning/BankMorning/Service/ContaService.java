package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;


@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Lazy
    @Autowired
    private UsuarioService usuarioService;

    public Conta converterParaModel(ContaRequest request) {
        if (request == null) {
            return null;
        }
        Conta conta = new Conta();
        BeanUtils.copyProperties(request, conta);

        return conta;
    }

    public ContaResponse converterParaResponse(Conta conta) {
        if (conta == null) {
            return null;
        }

        // Se este método for chamado dentro de um método @Transactional,
        // o acesso a conta.getUsuario() será resolvido (EAGER loading manual).
        UsuarioResponse usuarioResponse = conta.getUsuario() != null
                ? usuarioService.converterParaResponse(conta.getUsuario())
                : null;

        return new ContaResponse(
                conta.getIdConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getNumeroConta(),
                usuarioResponse
        );
    }

    // Os métodos de leitura abaixo garantem a transação aberta para o Lazy Loading:
    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorId(Integer id) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarSaldoPorCpf(String identificador) {
        Conta conta = contaRepository.findByUsuario_Email(identificador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada para o usuário logado."));

        return conta.getSaldo();
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorCpfUsuario(String cpf) {
        Conta conta = contaRepository.findByUsuario_Cpf(cpf).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorEmailUsuario(String email) {
        Conta conta = contaRepository.findByUsuario_Email(email).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    // 💥 NOVO MÉTODO ADICIONADO (CORREÇÃO FINAL para o Controller/Service):
    @Transactional(readOnly = true)
    public Conta buscarContaModelPorEmailUsuario(String email) {
        // Busca a conta e o usuário aninhado usando o repositório
        Conta conta = contaRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada para o usuário logado."));

        // Retorna o objeto Model (Conta) que o TransacaoController precisa.
        // A transação aberta garante que o Usuário (LAZY) esteja disponível se necessário.
        return conta;
    }

    @Transactional
    public ContaResponse criarConta(Usuario usuario, ContaRequest contaRequest) {
        if (contaRepository.findByUsuario_Cpf(usuario.getCpf()).isPresent()) {
            throw new ArgumentoInvalidoException("Usuario já possui uma conta");
        }

        Conta conta = new Conta();
        conta.setSenha(contaRequest.senha());
        conta.setUsuario(usuario);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta(Integer.toString(new Random().nextInt(90000) + 10000));

        Conta contaSalva = contaRepository.save(conta);

        return converterParaResponse(contaSalva);
    }
}