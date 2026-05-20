package com.verdenovo.api.dto;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String nivelAcesso;
    private String statusUsuario;

    public UsuarioResponse(Long id, String nome, String email, String nivelAcesso, String statusUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nivelAcesso = nivelAcesso;
        this.statusUsuario = statusUsuario;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getNivelAcesso() { return nivelAcesso; }
    public String getStatusUsuario() { return statusUsuario; }
}