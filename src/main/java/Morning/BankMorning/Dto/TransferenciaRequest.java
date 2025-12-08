package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class TransferenciaRequest {

    private String numeroContaOrigem;
    private String numeroContaDestino;
    private BigDecimal valor;

    // Construtor Vazio
    public TransferenciaRequest() {}

    // Construtor com Argumentos (Para o Teste)
    public TransferenciaRequest(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        this.numeroContaOrigem = numeroContaOrigem;
        this.numeroContaDestino = numeroContaDestino;
        this.valor = valor;
    }

    // Getters e Setters
    public String getNumeroContaOrigem() { return numeroContaOrigem; }
    public void setNumeroContaOrigem(String numeroContaOrigem) { this.numeroContaOrigem = numeroContaOrigem; }

    public String getNumeroContaDestino() { return numeroContaDestino; }
    public void setNumeroContaDestino(String numeroContaDestino) { this.numeroContaDestino = numeroContaDestino; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}