package com.verdenovo.api.service;

import com.verdenovo.api.entity.Ponto;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PontoVerificationService {
    private final CnpjConsultaService cnpjConsultaService;

    public PontoVerificationService(CnpjConsultaService cnpjConsultaService) {
        this.cnpjConsultaService = cnpjConsultaService;
    }

    public VerificationDecision verificar(Ponto ponto, boolean emailConfirmado) {
        CnpjConsultaService.CnpjConsultaResult consulta = cnpjConsultaService.consultar(ponto.getCnpj());
        ponto.setDataVerificacao(LocalDateTime.now());

        if (consulta.isFalha()) {
            aplicar(ponto, "PENDENTE_REVISAO", consulta.getMotivo(), "CNPJ");
            return new VerificationDecision(false);
        }

        if (!consulta.isEncontrado()) {
            aplicar(ponto, "NAO_VERIFICADO", "CNPJ nao encontrado.", "CNPJ");
            return new VerificationDecision(false);
        }

        CnpjPublicData dados = consulta.getDados();
        List<String> motivos = new ArrayList<>();

        if (!isAtivo(dados.getSituacao())) {
            motivos.add("CNPJ inativo.");
        }
        if (!nomeCompativel(ponto.getNome(), dados)) {
            motivos.add("Razao social ou nome fantasia divergente.");
        }
        if (!localizacaoCompativel(ponto, dados)) {
            motivos.add("Cidade, UF ou endereco divergente.");
        }
        if (!emailConfirmado) {
            motivos.add("Email ainda nao confirmado.");
        }

        ponto.setFonteVerificacao(dados.getFonte());
        if (motivos.isEmpty()) {
            aplicar(ponto, "VERIFICADO", "CNPJ ativo e dados cadastrais conferem.", dados.getFonte());
            return new VerificationDecision(true);
        }

        aplicar(ponto, "PENDENTE_REVISAO", String.join(" ", motivos), dados.getFonte());
        return new VerificationDecision(false);
    }

    private void aplicar(Ponto ponto, String status, String motivo, String fonte) {
        ponto.setStatusVerificacao(status);
        ponto.setMotivoVerificacao(motivo);
        ponto.setFonteVerificacao(fonte);
    }

    private boolean isAtivo(String situacao) {
        String valor = normalizarTexto(situacao);
        return valor.equals("ATIVA") || valor.equals("ATIVO");
    }

    private boolean nomeCompativel(String nomeInformado, CnpjPublicData dados) {
        String nome = normalizarTexto(nomeInformado);
        if (nome.isBlank()) return false;
        String razao = normalizarTexto(dados.getRazaoSocial());
        String fantasia = normalizarTexto(dados.getNomeFantasia());
        return contemComTamanhoSeguro(razao, nome) || contemComTamanhoSeguro(nome, razao)
                || contemComTamanhoSeguro(fantasia, nome) || contemComTamanhoSeguro(nome, fantasia);
    }

    private boolean localizacaoCompativel(Ponto ponto, CnpjPublicData dados) {
        boolean cepBate = !dados.getCep().isBlank() && dados.getCep().equals(CnpjUtils.normalizar(ponto.getCep()));
        boolean numeroBate = dados.getNumero() == null || dados.getNumero().isBlank()
                || normalizarTexto(dados.getNumero()).equals(normalizarTexto(ponto.getNumero()));
        if (cepBate && numeroBate) return true;

        String enderecoInformado = normalizarTexto(String.join(" ",
                texto(ponto.getLogradouro()),
                texto(ponto.getBairro()),
                texto(ponto.getCidade()),
                texto(ponto.getEstado())
        ));
        if (enderecoInformado.isBlank()) return false;

        boolean cidadeBate = dados.getMunicipio() != null
                && !dados.getMunicipio().isBlank()
                && enderecoInformado.contains(normalizarTexto(dados.getMunicipio()));
        boolean ufBate = dados.getUf() != null
                && !dados.getUf().isBlank()
                && enderecoInformado.contains(normalizarTexto(dados.getUf()));
        boolean logradouroBate = dados.getLogradouro() == null
                || dados.getLogradouro().isBlank()
                || contemComTamanhoSeguro(enderecoInformado, normalizarTexto(dados.getLogradouro()));

        return cidadeBate && ufBate && logradouroBate && numeroBate;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private boolean contemComTamanhoSeguro(String texto, String parte) {
        if (texto == null || parte == null) return false;
        String textoNormalizado = normalizarTexto(texto);
        String parteNormalizada = normalizarTexto(parte);
        return parteNormalizada.length() >= 4 && textoNormalizado.contains(parteNormalizada);
    }

    private String normalizarTexto(String valor) {
        if (valor == null) return "";
        String semAcento = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static class VerificationDecision {
        private final boolean aprovadoAutomaticamente;

        public VerificationDecision(boolean aprovadoAutomaticamente) {
            this.aprovadoAutomaticamente = aprovadoAutomaticamente;
        }

        public boolean isAprovadoAutomaticamente() {
            return aprovadoAutomaticamente;
        }
    }
}
