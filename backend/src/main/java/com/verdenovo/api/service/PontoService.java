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

    @Autowired
    private PontoVerificationService pontoVerificationService;

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
        ponto.setBairro(pontoAtualizado.getBairro());
        ponto.setCidade(pontoAtualizado.getCidade());
        ponto.setEstado(normalizarEstado(pontoAtualizado.getEstado()));
        ponto.setTelefone(pontoAtualizado.getTelefone());
        ponto.setHoraFuncionamento(pontoAtualizado.getHoraFuncionamento());
        ponto.setMaterial(pontoAtualizado.getMaterial());
        ponto.setDescricao(pontoAtualizado.getDescricao());
        return pontoRepository.save(ponto);
    }

    public Ponto buscarPontoLogado(String emailLogado) {
        if (emailLogado == null || emailLogado.isBlank()) {
            throw new RuntimeException("Ponto nao autenticado.");
        }
        return pontoRepository.findFirstByEmailIgnoreCase(emailLogado)
                .orElseThrow(() -> new RuntimeException("Ponto nao encontrado para este login."));
    }

    public Ponto atualizarPontoLogado(Ponto pontoAtualizado, String emailLogado) {
        Ponto ponto = buscarPontoLogado(emailLogado);
        boolean enderecoAlterado = enderecoAlterado(ponto, pontoAtualizado);

        ponto.setNome(pontoAtualizado.getNome());
        ponto.setCep(normalizarCep(pontoAtualizado.getCep()));
        ponto.setNumero(pontoAtualizado.getNumero());
        ponto.setComplemento(pontoAtualizado.getComplemento());
        ponto.setLogradouro(pontoAtualizado.getLogradouro());
        ponto.setBairro(pontoAtualizado.getBairro());
        ponto.setCidade(pontoAtualizado.getCidade());
        ponto.setEstado(normalizarEstado(pontoAtualizado.getEstado()));
        ponto.setTelefone(pontoAtualizado.getTelefone());
        ponto.setHoraFuncionamento(pontoAtualizado.getHoraFuncionamento());
        ponto.setMaterial(pontoAtualizado.getMaterial());
        ponto.setDescricao(pontoAtualizado.getDescricao());

        if (enderecoAlterado) {
            revalidarEnderecoAlterado(ponto);
        }

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

    private boolean enderecoAlterado(Ponto atual, Ponto novo) {
        return mudou(atual.getCep(), normalizarCep(novo.getCep()))
                || mudou(atual.getNumero(), novo.getNumero())
                || mudou(atual.getLogradouro(), novo.getLogradouro())
                || mudou(atual.getBairro(), novo.getBairro())
                || mudou(atual.getCidade(), novo.getCidade())
                || mudou(atual.getEstado(), normalizarEstado(novo.getEstado()));
    }

    private boolean mudou(String atual, String novo) {
        String a = atual == null ? "" : atual.trim();
        String n = novo == null ? "" : novo.trim();
        return !a.equalsIgnoreCase(n);
    }

    private String normalizarCep(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("\\D", "");
    }

    private String normalizarEstado(String estado) {
        if (estado == null) return null;
        String normalizado = estado.replaceAll("[^A-Za-z]", "").toUpperCase();
        return normalizado.length() > 2 ? normalizado.substring(0, 2) : normalizado;
    }

    private void revalidarEnderecoAlterado(Ponto ponto) {
        if (ponto.getCnpj() == null || ponto.getCnpj().isBlank()) {
            ponto.setStatusVerificacao("PENDENTE_REVISAO");
            ponto.setMotivoVerificacao("Endereco alterado em ponto antigo sem CNPJ cadastrado.");
            ponto.setDataVerificacao(LocalDateTime.now());
            ponto.setFonteVerificacao("Sistema");
            ponto.setStatusPonto("PENDENTE");
            return;
        }

        PontoVerificationService.VerificationDecision decisao = pontoVerificationService.verificar(
                ponto,
                emailConfirmadoOuNaoExigido(ponto)
        );
        ponto.setStatusPonto(decisao.isAprovadoAutomaticamente() ? "ATIVO" : "PENDENTE");
    }

    public Ponto criarPonto(Ponto ponto, String emailLogado) {
        validarCnpjNovoPonto(ponto);

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

        boolean criadoPorAdmin = emailLogado != null && usuarioRepository.findByEmail(emailLogado)
                .map(u -> "ADMIN".equals(u.getNivelAcesso()))
                .orElse(false);
        PontoVerificationService.VerificationDecision decisao = pontoVerificationService.verificar(
                ponto,
                emailConfirmadoOuNaoExigido(ponto)
        );
        if (decisao.isAprovadoAutomaticamente() || criadoPorAdmin) {
            ponto.setStatusPonto("ATIVO");
        } else {
            ponto.setStatusPonto("PENDENTE");
        }

        return pontoRepository.save(ponto);
    }

    private void validarCnpjNovoPonto(Ponto ponto) {
        String cnpj = CnpjUtils.normalizar(ponto.getCnpj());
        if (cnpj.isBlank()) {
            throw new RuntimeException("CNPJ e obrigatorio para cadastrar um ponto de coleta.");
        }
        if (!CnpjUtils.isValido(cnpj)) {
            throw new RuntimeException("CNPJ invalido. Confira os 14 digitos informados.");
        }
        if (pontoRepository.existsByCnpj(cnpj)) {
            throw new RuntimeException("Ja existe um ponto cadastrado com este CNPJ.");
        }
        ponto.setCnpj(cnpj);
    }

    private boolean emailConfirmadoOuNaoExigido(Ponto ponto) {
        if (ponto.getUsuarioId() != null) {
            return usuarioRepository.findById(ponto.getUsuarioId())
                    .map(u -> "ATIVO".equals(u.getStatusUsuario()))
                    .orElse(true);
        }
        if (ponto.getEmail() != null && !ponto.getEmail().isBlank()) {
            return usuarioRepository.findByEmail(ponto.getEmail())
                    .map(u -> "ATIVO".equals(u.getStatusUsuario()))
                    .orElse(true);
        }
        return true;
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
