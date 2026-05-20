package com.verdenovo.api.config;

import com.verdenovo.api.entity.Categoria;
import com.verdenovo.api.entity.Usuario;
import com.verdenovo.api.repository.CategoriaRepository;
import com.verdenovo.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.senha}")
    private String adminSenha;
    
    @Override
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0) {
            Categoria categoria = new Categoria();
            categoria.setNome("Geral");
            categoria.setDescricao("Categoria padrão para pontos de coleta");
            categoria.setStatusCategoria("ATIVO");
            categoriaRepository.save(categoria);
        }
        
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setNivelAcesso("ADMIN");
            admin.setStatusUsuario("ATIVO");
            admin.setDataCadastro(LocalDateTime.now());
            usuarioRepository.save(admin);
        }
    }
}