package com.verdenovo.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Optional;

@Service
public class CnpjConsultaService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final boolean enabled;

    public CnpjConsultaService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${cnpj.api.base-url:https://brasilapi.com.br/api/cnpj/v1}") String baseUrl,
            @Value("${cnpj.api.timeout-ms:3000}") int timeoutMs,
            @Value("${cnpj.api.enabled:true}") boolean enabled) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
        this.baseUrl = removerBarraFinal(baseUrl);
        this.enabled = enabled;
    }

    public CnpjConsultaResult consultar(String cnpj) {
        if (!enabled) {
            return CnpjConsultaResult.falha("API de CNPJ desativada.");
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(cnpj)
                .toUriString();

        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null || body.isEmpty()) {
                return CnpjConsultaResult.falha("API de CNPJ indisponivel.");
            }
            return CnpjConsultaResult.encontrado(mapearBrasilApi(body));
        } catch (HttpClientErrorException.NotFound e) {
            return CnpjConsultaResult.naoEncontrado("CNPJ nao encontrado.");
        } catch (RestClientException e) {
            return CnpjConsultaResult.falha("API de CNPJ indisponivel.");
        }
    }

    private CnpjPublicData mapearBrasilApi(JsonNode body) {
        CnpjPublicData data = new CnpjPublicData();
        data.setRazaoSocial(texto(body, "razao_social").orElse(texto(body, "razaoSocial").orElse("")));
        data.setNomeFantasia(texto(body, "nome_fantasia").orElse(texto(body, "nomeFantasia").orElse("")));
        data.setSituacao(texto(body, "descricao_situacao_cadastral")
                .orElse(texto(body, "situacao").orElse(texto(body, "situacaoCadastral").orElse(""))));
        data.setMunicipio(texto(body, "municipio").orElse(texto(body, "cidade").orElse("")));
        data.setUf(texto(body, "uf").orElse(""));
        data.setCep(CnpjUtils.normalizar(texto(body, "cep").orElse("")));
        data.setLogradouro(texto(body, "logradouro").orElse(""));
        data.setNumero(texto(body, "numero").orElse(""));
        data.setFonte("BrasilAPI");
        return data;
    }

    private Optional<String> texto(JsonNode body, String field) {
        JsonNode value = body.get(field);
        if (value == null || value.isNull()) return Optional.empty();
        String text = value.asText("").trim();
        return text.isEmpty() ? Optional.empty() : Optional.of(text);
    }

    private String removerBarraFinal(String value) {
        if (value == null || value.isBlank()) return "https://brasilapi.com.br/api/cnpj/v1";
        return value.replaceAll("/+$", "");
    }

    public static class CnpjConsultaResult {
        private final boolean encontrado;
        private final boolean falha;
        private final String motivo;
        private final CnpjPublicData dados;

        private CnpjConsultaResult(boolean encontrado, boolean falha, String motivo, CnpjPublicData dados) {
            this.encontrado = encontrado;
            this.falha = falha;
            this.motivo = motivo;
            this.dados = dados;
        }

        public static CnpjConsultaResult encontrado(CnpjPublicData dados) {
            return new CnpjConsultaResult(true, false, "", dados);
        }

        public static CnpjConsultaResult naoEncontrado(String motivo) {
            return new CnpjConsultaResult(false, false, motivo, null);
        }

        public static CnpjConsultaResult falha(String motivo) {
            return new CnpjConsultaResult(false, true, motivo, null);
        }

        public boolean isEncontrado() { return encontrado; }
        public boolean isFalha() { return falha; }
        public String getMotivo() { return motivo; }
        public CnpjPublicData getDados() { return dados; }
    }
}
