package Morning.BankMorning.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conta")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConta;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String agencia = "777";

    @Column(name = "numero_conta")
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // CORREÇÃO: Removendo @OnDelete (O CascadeType.ALL em Usuario já lida com a deleção)
    // Manter FetchType.EAGER é crucial para o login funcionar!
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false) // A Conta é a proprietária da chave
    @JsonIgnore
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}