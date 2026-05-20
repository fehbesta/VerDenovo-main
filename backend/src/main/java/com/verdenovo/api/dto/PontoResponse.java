package com.verdenovo.api.dto;

public class PontoResponse {
    private Long id;
    private String nome;
    private String email;
    private String cep;
    private String numero;
    private String complemento;
    private String logradouro;
    private String telefone;
    private String horaFuncionamento;
    private String material;
    
    public PontoResponse(Long id, String nome, String email, String cep, String numero,
                        String complemento, String logradouro, String telefone,
                        String horaFuncionamento, String material) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
        this.logradouro = logradouro;
        this.telefone = telefone;
        this.horaFuncionamento = horaFuncionamento;
        this.material = material;
    }
    
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCep() { return cep; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getLogradouro() { return logradouro; }
    public String getTelefone() { return telefone; }
    public String getHoraFuncionamento() { return horaFuncionamento; }
    public String getMaterial() { return material; }
}