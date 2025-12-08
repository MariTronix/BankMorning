package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SaqueRequest {

    @NotBlank(message = "O número da conta é obrigatório.")
    private String numeroConta;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private BigDecimal valor;

    // --- 1. CONSTRUTOR VAZIO ---
    // Necessário para o Jackson (JSON -> Objeto) funcionar
    public SaqueRequest() {}

    // --- 2. CONSTRUTOR PARA TESTES ---
    // Permite: new SaqueRequest("12345", valor);
    public SaqueRequest(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

    // --- GETTERS E SETTERS ---
    public String getNumeroConta() { 
        return numeroConta; 
    }
    
    public void setNumeroConta(String numeroConta) { 
        this.numeroConta = numeroConta; 
    }

    public BigDecimal getValor() { 
        return valor; 
    }
    
    public void setValor(BigDecimal valor) { 
        this.valor = valor; 
    }
}