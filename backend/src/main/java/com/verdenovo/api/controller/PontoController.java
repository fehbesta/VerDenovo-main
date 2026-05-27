package com.verdenovo.api.controller;

import com.verdenovo.api.entity.Ponto;
import com.verdenovo.api.repository.PontoRepository;
import com.verdenovo.api.repository.UsuarioRepository;
import com.verdenovo.api.dto.*;
import com.verdenovo.api.service.PontoService;
import com.verdenovo.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pontos")
public class PontoController {
    
    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PontoService pontoService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Ponto> listarPontos() {
        return pontoRepository.findByStatusPonto("ATIVO");
    }
    
    @GetMapping("/todos")
    public List<Ponto> listarTodosPontos() {
        return pontoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<MessageResponse> criarPonto(@RequestBody Ponto ponto,
            org.springframework.security.core.Authentication authentication) {
        String emailLogado = authentication != null ? authentication.getName() : null;
        Ponto salvo = pontoService.criarPonto(ponto, emailLogado);
        boolean isAtivo = "ATIVO".equals(salvo.getStatusPonto());
        return ResponseEntity.ok(new MessageResponse(
            isAtivo ? "Ponto cadastrado e ativado com sucesso!" : "Ponto cadastrado com sucesso! Aguardando aprovação do administrador."
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> atualizarPonto(@PathVariable Long id, @RequestBody Ponto pontoAtualizado,
            org.springframework.security.core.Authentication authentication) {
        String emailLogado = authentication != null ? authentication.getName() : null;
        pontoService.atualizarPonto(id, pontoAtualizado, emailLogado);
        return ResponseEntity.ok(new MessageResponse("Ponto atualizado com sucesso"));
    }

    @GetMapping("/pendentes")
    public List<Ponto> listarPontosPendentes() {
        return pontoRepository.findByStatusPonto("PENDENTE");
    }

    @GetMapping("/meus")
    public ResponseEntity<?> listarMeusPontos(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
            .map(u -> ResponseEntity.ok(pontoRepository.findByUsuarioId(u.getId())))
            .orElse(ResponseEntity.ok(java.util.List.of()));
    }

    @GetMapping("/me")
    public ResponseEntity<Ponto> buscarMeuPonto(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(pontoService.buscarPontoLogado(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<Ponto> atualizarMeuPonto(@RequestBody Ponto pontoAtualizado,
            org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        Ponto salvo = pontoService.atualizarPontoLogado(pontoAtualizado, authentication.getName());
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<MessageResponse> aprovarPonto(@PathVariable Long id) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));
        ponto.setStatusPonto("ATIVO");
        pontoRepository.save(ponto);
        return ResponseEntity.ok(new MessageResponse("Ponto aprovado com sucesso"));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<MessageResponse> rejeitarPonto(@PathVariable Long id) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));
        ponto.setStatusPonto("REJEITADO");
        pontoRepository.save(ponto);
        return ResponseEntity.ok(new MessageResponse("Ponto rejeitado"));
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> alterarStatusPonto(@PathVariable Long id) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));
        String novoStatus = "ATIVO".equals(ponto.getStatusPonto()) ? "INATIVO" : "ATIVO";
        ponto.setStatusPonto(novoStatus);
        pontoRepository.save(ponto);
        return ResponseEntity.ok(new MessageResponse("Status do ponto alterado com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletarPonto(@PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {
        String emailLogado = authentication != null ? authentication.getName() : null;
        pontoService.deletarPonto(id, emailLogado);
        return ResponseEntity.ok(new MessageResponse("Ponto excluído com sucesso"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<PontoLoginResponse> loginPonto(@RequestBody PontoLoginRequest request) {
        Ponto ponto = pontoService.loginPonto(request.getEmail(), request.getSenha());
        String token = jwtUtil.generatePontoToken(ponto.getEmail());
        PontoResponse pontoResponse = new PontoResponse(
                ponto.getId(), ponto.getNome(), ponto.getEmail(),
                ponto.getCep(), ponto.getNumero(), ponto.getComplemento(),
                ponto.getLogradouro(), ponto.getTelefone(),
                ponto.getHoraFuncionamento(), ponto.getMaterial()
        );
        return ResponseEntity.ok(new PontoLoginResponse(token, pontoResponse));
    }
}
