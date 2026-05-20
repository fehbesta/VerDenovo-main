package com.verdenovo.api.service;

import com.verdenovo.api.entity.Ponto;
import com.verdenovo.api.entity.Usuario;
import com.verdenovo.api.repository.PontoRepository;
import com.verdenovo.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    private final java.security.SecureRandom secureRandom = new java.security.SecureRandom();

    private String gerarCodigo() {
        return String.format("%06d", secureRandom.nextInt(999999));
    }

    public void solicitarRecuperacao(String email) {
        validarEmailConfigurado();

        String codigo = gerarCodigo();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        java.util.Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setResetCode(codigo);
            usuario.setResetToken(null);
            usuario.setResetTokenExpiry(expiry);
            usuarioRepository.save(usuario);
            enviarEmailCodigo(email, usuario.getNome(), codigo);
            return;
        }

        Ponto ponto = pontoRepository.findFirstByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("Email nao encontrado"));
        ponto.setResetCode(codigo);
        ponto.setResetToken(null);
        ponto.setResetTokenExpiry(expiry);
        pontoRepository.save(ponto);
        enviarEmailCodigo(email, ponto.getNome(), codigo);
    }

    public void verificarCodigo(String email, String codigo) {
        java.util.Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            validarCodigo(usuario.getResetCode(), usuario.getResetTokenExpiry(), codigo);
            return;
        }

        Ponto ponto = pontoRepository.findFirstByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("Email nao encontrado"));
        validarCodigo(ponto.getResetCode(), ponto.getResetTokenExpiry(), codigo);
    }

    public void redefinirSenhaPorCodigo(String email, String codigo, String novaSenha) {
        java.util.Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            validarCodigo(usuario.getResetCode(), usuario.getResetTokenExpiry(), codigo);
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            usuario.setResetCode(null);
            usuario.setResetToken(null);
            usuario.setResetTokenExpiry(null);
            usuarioRepository.save(usuario);
            return;
        }

        Ponto ponto = pontoRepository.findFirstByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("Email nao encontrado"));
        validarCodigo(ponto.getResetCode(), ponto.getResetTokenExpiry(), codigo);
        ponto.setSenha(passwordEncoder.encode(novaSenha));
        ponto.setResetCode(null);
        ponto.setResetToken(null);
        ponto.setResetTokenExpiry(null);
        pontoRepository.save(ponto);
    }

    public void redefinirSenha(String token, String novaSenha) {
        java.util.Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            validarToken(usuario.getResetTokenExpiry());
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            usuario.setResetToken(null);
            usuario.setResetTokenExpiry(null);
            usuarioRepository.save(usuario);
            return;
        }

        Ponto ponto = pontoRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token invalido"));
        validarToken(ponto.getResetTokenExpiry());
        ponto.setSenha(passwordEncoder.encode(novaSenha));
        ponto.setResetToken(null);
        ponto.setResetTokenExpiry(null);
        pontoRepository.save(ponto);
    }

    private void validarCodigo(String resetCode, LocalDateTime expiry, String codigo) {
        if (resetCode == null || !resetCode.equals(codigo)) {
            throw new RuntimeException("Codigo invalido.");
        }
        if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Codigo expirado. Solicite um novo.");
        }
    }

    private void validarToken(LocalDateTime expiry) {
        if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado. Solicite uma nova recuperacao.");
        }
    }

    private void enviarEmailCodigo(String destinatario, String nome, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemetente);
            helper.setTo(destinatario);
            helper.setSubject("VerDenovo - Codigo de Recuperacao de Senha");

            String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <div style="background: linear-gradient(135deg, #10b981, #059669); padding: 2rem; text-align: center; border-radius: 12px 12px 0 0;">
                    <h1 style="color: white; margin: 0;">VerDenovo</h1>
                  </div>
                  <div style="background: #f9fafb; padding: 2rem; border-radius: 0 0 12px 12px;">
                    <h2 style="color: #1f2937;">Ola, %s!</h2>
                    <p style="color: #4b5563;">Recebemos uma solicitacao para redefinir a senha da sua conta.</p>
                    <p style="color: #4b5563;">Use o codigo abaixo para continuar. Ele e valido por <strong>15 minutos</strong>.</p>
                    <div style="text-align: center; margin: 2rem 0;">
                      <div style="display: inline-block; background: white; border: 2px solid #10b981; border-radius: 12px; padding: 1rem 2.5rem;">
                        <span style="font-size: 2.5rem; font-weight: bold; letter-spacing: 0.5rem; color: #059669;">%s</span>
                      </div>
                    </div>
                    <p style="color: #9ca3af; font-size: 13px;">Se voce nao solicitou isso, ignore este email. Sua senha permanece a mesma.</p>
                  </div>
                </div>
                """.formatted(nome, codigo);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Nao foi possivel enviar o codigo agora. Tente novamente mais tarde.", e);
        }
    }

    private void validarEmailConfigurado() {
        if (emailRemetente == null || emailRemetente.isBlank()) {
            throw new RuntimeException("Servico de email nao configurado. Tente novamente mais tarde.");
        }
    }
}
