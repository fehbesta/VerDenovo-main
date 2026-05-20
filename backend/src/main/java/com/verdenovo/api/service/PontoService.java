package com.verdenovo.api.service;

import com.verdenovo.api.entity.Ponto;
import com.verdenovo.api.repository.PontoRepository;
import com.verdenovo.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PontoService {

    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Ponto loginPonto(String email, String senha) {
        Ponto ponto = pontoRepository.findByEmailAndStatusPonto(email, "ATIVO")
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));
        if (ponto.getSenha() == null || !passwordEncoder.matches(senha, ponto.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        return ponto;
    }

    public Ponto atualizarPonto(Long id, Ponto pontoAtualizado, String emailLogado) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));
        if (!temPermissaoNoPonto(ponto, emailLogado)) {
            throw new RuntimeException("Sem permissão para editar este ponto.");
        }
        ponto.setNome(pontoAtualizado.getNome());
        ponto.setCep(pontoAtualizado.getCep());
        ponto.setNumero(pontoAtualizado.getNumero());
        ponto.setComplemento(pontoAtualizado.getComplemento());
        ponto.setLogradouro(pontoAtualizado.getLogradouro());
        ponto.setTelefone(pontoAtualizado.getTelefone());
        ponto.setHoraFuncionamento(pontoAtualizado.getHoraFuncionamento());
        ponto.setMaterial(pontoAtualizado.getMaterial());
        ponto.setDescricao(pontoAtualizado.getDescricao());
        return pontoRepository.save(ponto);
    }

    public void deletarPonto(Long id, String emailLogado) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));
        if (!temPermissaoNoPonto(ponto, emailLogado)) {
            throw new RuntimeException("Sem permissão para excluir este ponto.");
        }
        pontoRepository.deleteById(id);
    }

    private boolean temPermissaoNoPonto(Ponto ponto, String emailLogado) {
        if (emailLogado == null || emailLogado.isBlank()) return false;
        boolean isAdmin = usuarioRepository.findByEmail(emailLogado)
                .map(u -> "ADMIN".equals(u.getNivelAcesso()))
                .orElse(false);
        if (isAdmin) return true;
        Long usuarioId = usuarioRepository.findByEmail(emailLogado).map(u -> u.getId()).orElse(null);
        if (usuarioId != null && usuarioId.equals(ponto.getUsuarioId())) return true;
        return emailLogado.equalsIgnoreCase(ponto.getEmail());
    }

    public Ponto criarPonto(Ponto ponto, String emailLogado) {
        if (emailLogado != null) {
            usuarioRepository.findByEmail(emailLogado).ifPresent(u -> {
                if ("ADMIN".equals(u.getNivelAcesso())) {
                    vincularPontoAdmin(ponto, emailLogado);
                } else {
                    vincularPontoUsuario(ponto, u.getId());
                }
            });
        }

        codificarSenha(ponto);
        ponto.setDataCadastro(LocalDateTime.now());
        if (ponto.getStatusPonto() == null) ponto.setStatusPonto("PENDENTE");

        return pontoRepository.save(ponto);
    }

    private void vincularPontoAdmin(Ponto ponto, String emailLogado) {
        String emailDono = (ponto.getEmail() != null && !ponto.getEmail().isEmpty())
                ? ponto.getEmail() : emailLogado;
        usuarioRepository.findByEmail(emailDono)
                .ifPresent(dono -> ponto.setUsuarioId(dono.getId()));
        if (ponto.getEmail() == null || ponto.getEmail().isEmpty()) {
            ponto.setEmail(emailLogado);
        }
        ponto.setStatusPonto("ATIVO");
    }

    private void vincularPontoUsuario(Ponto ponto, Long usuarioId) {
        boolean jaTemPonto = pontoRepository.findByUsuarioId(usuarioId).stream()
                .anyMatch(p -> "ATIVO".equals(p.getStatusPonto()) || "PENDENTE".equals(p.getStatusPonto()));
        if (jaTemPonto) {
            throw new RuntimeException("Você já possui um ponto de coleta cadastrado.");
        }
        ponto.setUsuarioId(usuarioId);
        ponto.setStatusPonto("PENDENTE");
    }

    private void codificarSenha(Ponto ponto) {
        if (ponto.getSenha() != null && !ponto.getSenha().isEmpty()) {
            ponto.setSenha(passwordEncoder.encode(ponto.getSenha()));
        } else {
            ponto.setSenha(passwordEncoder.encode(java.util.UUID.randomUUID().toString().substring(0, 12)));
        }
    }
}
