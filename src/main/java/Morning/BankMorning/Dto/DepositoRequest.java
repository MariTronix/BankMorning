package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DepositoRequest {

    @NotBlank(message = "O número da conta é obrigatório.")
    private String numeroConta;

    private String agencia; // Opcional

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private BigDecimal valor;

    // --- 1. CONSTRUTOR VAZIO ---
    // Obrigatório para o Spring conseguir converter o JSON em Objeto
    public DepositoRequest() {
    }

    // --- 2. CONSTRUTOR PARA OS TESTES ---
    // Permite fazer: new DepositoRequest("12345", new BigDecimal("50.00"));
    public DepositoRequest(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

    // --- 3. CONSTRUTOR COMPLETO ---
    public DepositoRequest(String numeroConta, String agencia, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.valor = valor;
    }

    // --- GETTERS E SETTERS ---
    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}