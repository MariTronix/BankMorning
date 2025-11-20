package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ClienteRequest;
import Morning.BankMorning.Dto.ClienteResponse;
import Morning.BankMorning.Model.Cliente;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ClienteRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.aspectj.apache.bcel.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    ClienteRepository repository;

        public ClienteResponse criarCliente(ClienteRequest request) {
            if (repository.existsByCpf(request.getCpf())) {
                throw new IllegalArgumentException("Já existe um cliente com este CPF");
                }

            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setCpf(request.getCpf());
            cliente.setEmail(request.getEmail());
            cliente.setDataNascimento(request.getData_nascimento());

            Cliente clienteSalvo = repository.save(cliente);

            Usuario usuario = new Usuario();

            usuario.setLogin(request.getEmail());

            String senhaCriptografada = passwordEncoder.encode(request.getSenha());
            usuario.setSenha(senhaCriptografada);

            usuario.setCliente(clienteSalvo);
            usuarioRepository.save(usuario);

            ClienteResponse response = new ClienteResponse();
            response.setIdCliente(clienteSalvo.getIdCliente());
            response.setNome(clienteSalvo.getNome());
            response.setEmail(clienteSalvo.getEmail());

            return response;
            }
        }

