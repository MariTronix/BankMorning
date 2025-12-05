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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private static UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Conta converterParaModel(ContaRequest request) {
        if (request == null) {
            return null;
        }
        Conta conta = new Conta();
        BeanUtils.copyProperties(request, conta);

        return conta;
    }

    public   ContaResponse converterParaResponse(Conta conta) {
        if (conta == null) {
            return null;
        }

        UsuarioResponse usuarioResponse = usuarioService.converterParaResponse(conta.getUsuario());

        return new ContaResponse(conta.getIdConta(), conta.getAgencia(), conta.getSaldo(), usuarioResponse);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorId(Integer id) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
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

    @Transactional
    public ContaResponse criarConta(Usuario usuario, ContaRequest contaRequest) {
        if (contaRepository.findByUsuario_Cpf(usuario.getCpf()).isPresent()) {
            throw new ArgumentoInvalidoException("Usuario já possui uma conta");
        }

        Conta conta = new Conta();
        conta.setSenha(passwordEncoder.encode(contaRequest.senha()));
        conta.setUsuario(usuario);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta(Integer.toString(new Random().nextInt(90000) + 10000));

        usuario.setConta(conta);

        Conta contaSalva = contaRepository.save(conta);

        return converterParaResponse(contaSalva);
    }
}