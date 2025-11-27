package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ContaService contaService;


    public UsuarioResponse criarUsuario(UsuarioRequest request) {

        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este EMAIL");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setDataNascimento(request.getData_nascimento());

        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        usuario.setSenha(senhaCriptografada);

        Usuario salvo = usuarioRepository.save(usuario);

        contaService.criarContaParaUsuario(salvo);

        UsuarioResponse response = new UsuarioResponse();
        response.setIdUsuario(salvo.getIdUsuario());
        response.setNome(salvo.getNome());
        response.setEmail(salvo.getEmail());

        return response;
    }
}
