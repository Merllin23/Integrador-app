package com.jkmconfecciones.Integrador_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    @Value("${recaptcha.verify.url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Verifica el token de reCAPTCHA con Google
     * 
     * @param recaptchaResponse El token obtenido del formulario (g-recaptcha-response)
     * @param remoteIp IP del cliente (opcional pero recomendado)
     * @return true si el captcha es válido, false si no
     */
    public boolean verificarCaptcha(String recaptchaResponse, String remoteIp) {
        try {
            log.info("🔐 Verificando reCAPTCHA...");

            // Validaciones básicas
            if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
                log.warn("⚠️ Token de reCAPTCHA vacío o nulo");
                return false;
            }

            // Preparar la petición POST a Google
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", recaptchaResponse);
            
            if (remoteIp != null && !remoteIp.isEmpty()) {
                params.add("remoteip", remoteIp);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Llamar a la API de Google reCAPTCHA
            ResponseEntity<Map> response = restTemplate.postForEntity(
                verifyUrl,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Boolean success = (Boolean) body.get("success");
                
                if (Boolean.TRUE.equals(success)) {
                    log.info("✅ reCAPTCHA verificado exitosamente");
                    return true;
                } else {
                    log.warn("❌ reCAPTCHA inválido. Errores: {}", body.get("error-codes"));
                    return false;
                }
            }

            log.error("❌ Respuesta inesperada de Google reCAPTCHA");
            return false;

        } catch (Exception e) {
            log.error("❌ Error al verificar reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica el captcha sin IP (menos seguro pero funcional)
     */
    public boolean verificarCaptcha(String recaptchaResponse) {
        return verificarCaptcha(recaptchaResponse, null);
    }

    /**
     * Obtiene información detallada de la verificación (para debugging)
     */
    public Map<String, Object> verificarCaptchaDetallado(String recaptchaResponse, String remoteIp) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", recaptchaResponse);
            
            if (remoteIp != null) {
                params.add("remoteip", remoteIp);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                verifyUrl,
                request,
                Map.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error al verificar reCAPTCHA detallado: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
