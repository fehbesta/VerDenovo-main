package com.verdenovo.api.controller;

import com.verdenovo.api.dto.LoginRequest;
import com.verdenovo.api.dto.LoginResponse;
import com.verdenovo.api.dto.MessageResponse;
import com.verdenovo.api.dto.UsuarioResponse;
import com.verdenovo.api.entity.Usuario;
import com.verdenovo.api.service.AuthService;
import com.verdenovo.api.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<MessageResponse> cadastro(@RequestBody Usuario usuario) {
        authService.cadastrar(usuario);
        return ResponseEntity.ok(new MessageResponse("Usuário cadastrado com sucesso"));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(a -> "ADMIN".equals(a.getAuthority()))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @GetMapping("/usuarios/me")
    public ResponseEntity<UsuarioResponse> buscarMeuPerfil(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(authService.buscarUsuarioLogado(authentication.getName()));
    }

    @PutMapping("/usuarios/me")
    public ResponseEntity<UsuarioResponse> atualizarMeuPerfil(@RequestBody Usuario usuario,
            org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(authService.atualizarUsuarioLogado(authentication.getName(), usuario));
    }

    @PutMapping("/usuarios/{id}/status")
    public ResponseEntity<MessageResponse> alterarStatusUsuario(@PathVariable Long id) {
        authService.alterarStatusUsuario(id);
        return ResponseEntity.ok(new MessageResponse("Status do usuário alterado com sucesso"));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<MessageResponse> deletarUsuario(@PathVariable Long id) {
        authService.deletarUsuario(id);
        return ResponseEntity.ok(new MessageResponse("Usuário deletado com sucesso"));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<MessageResponse> recuperarSenha(@RequestBody Map<String, String> body) {
        passwordResetService.solicitarRecuperacao(body.get("email"));
        return ResponseEntity.ok(new MessageResponse("Código enviado para o email"));
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<MessageResponse> verificarCodigo(@RequestBody Map<String, String> body) {
        passwordResetService.verificarCodigo(body.get("email"), body.get("codigo"));
        return ResponseEntity.ok(new MessageResponse("Código válido"));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<MessageResponse> redefinirSenha(@RequestBody Map<String, String> body) {
        // Suporta fluxo por código (novo) e por token (legado)
        if (body.get("codigo") != null) {
            passwordResetService.redefinirSenhaPorCodigo(body.get("email"), body.get("codigo"), body.get("novaSenha"));
        } else {
            passwordResetService.redefinirSenha(body.get("token"), body.get("novaSenha"));
        }
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso"));
    }
}
