package com.verdenovo.api.service;

import com.verdenovo.api.dto.LoginRequest;
import com.verdenovo.api.dto.LoginResponse;
import com.verdenovo.api.dto.UsuarioResponse;
import com.verdenovo.api.entity.Usuario;
import com.verdenovo.api.repository.UsuarioRepository;
import com.verdenovo.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndStatusUsuario(request.getEmail(), "ATIVO")
            .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));
        
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        
        String token = jwtUtil.generateToken(usuario.getEmail());
        return new LoginResponse(token, new UsuarioResponse(
            usuario.getId(), usuario.getNome(), usuario.getEmail(),
            usuario.getNivelAcesso(), usuario.getStatusUsuario()));
    }

    public void cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setStatusUsuario("ATIVO");
        usuario.setNivelAcesso("USER");
        
        usuarioRepository.save(usuario);
    }
    
    public java.util.List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
            .map(u -> new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(),
                u.getNivelAcesso(), u.getStatusUsuario()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public void alterarStatusUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if ("ADMIN".equals(usuario.getNivelAcesso())) {
            throw new RuntimeException("Não é possível alterar status de administradores");
        }
        
        String novoStatus = "ATIVO".equals(usuario.getStatusUsuario()) ? "INATIVO" : "ATIVO";
        usuario.setStatusUsuario(novoStatus);
        usuarioRepository.save(usuario);
    }
    
    public void deletarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if ("ADMIN".equals(usuario.getNivelAcesso())) {
            throw new RuntimeException("Não é possível deletar usuários administradores");
        }
        
        usuarioRepository.delete(usuario);
    }
}