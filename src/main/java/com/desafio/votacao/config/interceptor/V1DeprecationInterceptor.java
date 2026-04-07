package com.desafio.votacao.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class V1DeprecationInterceptor implements HandlerInterceptor {

    private static final String WARNING_MESSAGE = "299 - \"A versao /api/v1/ desta API esta depreciada e sera removida no futuro. Por favor, migre para /api/v2/.\"";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getRequestURI().startsWith("/api/v1/")) {
            log.warn("Requisicao para API legada detectada: URI={}. Enviando aviso de depreciação no cabeçalho.", 
                    request.getRequestURI());
            response.addHeader("Warning", WARNING_MESSAGE);
            response.addHeader("X-API-Deprecated", "true");
        }
        return true;
    }
}
