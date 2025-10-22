package com.jkmconfecciones.Integrador_app.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.*;
import java.util.*;

@Component
public class TokenManager {

    private final Map<String, String> tokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Genera y almacena el token por 15 minutos
    public String generarToken(String correo) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, correo);

        scheduler.schedule(() -> tokens.remove(token), 15, TimeUnit.MINUTES);

        return token;
    }

    public String obtenerCorreoPorToken(String token) {
        return tokens.get(token);
    }

    public void eliminarToken(String token) {
        tokens.remove(token);
    }
}
