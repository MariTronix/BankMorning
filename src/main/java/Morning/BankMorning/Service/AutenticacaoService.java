package Morning.BankMorning.Service;

import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
//
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Optional<Usuario> usuarioCpf = usuarioRepository.findByCpf(login);
        if (usuarioCpf.isPresent()) {
            return usuarioCpf.get();
        }

        Optional<Usuario> usuarioEmail = usuarioRepository.findByEmail(login);
        if (usuarioEmail.isPresent()) {
            return usuarioEmail.get();
        }

        throw new UsernameNotFoundException("Usuário não encontrado com o login: " + login);
    }
}