package Morning.BankMorning.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    // --- ATRIBUTOS DO BANCO DE DADOS (Não apague estes!) ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false)
    private String senha;

    @OneToOne
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Define que todo usuário tem o perfil padrão de "ROLE_USER"
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //A conta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //A conta nunca é bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; //A senha nunca expira
    }

    @Override
    public boolean isEnabled() {
        return true; //O usuário está sempre ativo
    }
}