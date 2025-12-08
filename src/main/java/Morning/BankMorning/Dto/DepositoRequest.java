package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class DepositoRequest {

    private String numeroConta;
    private String agencia;
    private BigDecimal valor;

    // --- 1. CONSTRUTOR VAZIO (Obrigatório para o Spring funcionar) ---
    public DepositoRequest() {
    }

    // --- 2. CONSTRUTOR PARA O TESTE (Aceita 2 argumentos) ---
    // O teste chama: new DepositoRequest("123", valor);
    public DepositoRequest(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

    // --- 3. CONSTRUTOR COMPLETO (Opcional, caso queira usar depois) ---
    public DepositoRequest(String numeroConta, String agencia, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.valor = valor;
    }

    // --- Getters e Setters ---
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}