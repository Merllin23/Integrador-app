package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.entidades.Rol;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.RolRepositorio;
import com.jkmconfecciones.Integrador_app.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class RegistroServiceImpl implements RegistroService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Control temporal de IPs
    private final Map<String, Integer> registrosPorIp = new HashMap<>();
    private final Map<String, LocalDateTime> ultimoIntentoIp = new HashMap<>();

    private static final Pattern CORREO_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public String registrarUsuario(Usuario usuario, String captchaToken, String ip) {

        // Validar CAPTCHA
        if (captchaToken == null || captchaToken.isBlank()) {
            return "Por favor, completa el CAPTCHA.";
        }

        // Limitar registros por IP
        registrosPorIp.putIfAbsent(ip, 0);
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime ultimo = ultimoIntentoIp.get(ip);

        if (ultimo != null && ultimo.isAfter(ahora.minusMinutes(1))) {
            return "Debes esperar 1 minuto antes de volver a intentar.";
        }

        if (registrosPorIp.get(ip) >= 3) {
            return "Límite de cuentas por IP alcanzado. Intenta más tarde.";
        }

        // Validaciones básicas
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return "El nombre es obligatorio.";
        }

        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            return "El correo es obligatorio.";
        }

        // Validación real del formato del correo
        if (!CORREO_REGEX.matcher(usuario.getCorreo().trim()).matches()) {
            return "Por favor ingresa un correo electrónico válido.";
        }

        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            return "El correo ya está registrado.";
        }

        // Validación de contraseña segura
        if (!esContrasenaSegura(usuario.getContraseña())) {
            return "La contraseña debe tener al menos 8 caracteres, un número y una letra.";
        }

        // Configurar datos del nuevo usuario
        Rol rolUsuario = rolRepositorio.findAll().stream()
                .filter(r -> r.getNombreRol().equalsIgnoreCase("Usuario"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rol 'Usuario' no encontrado"));

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setRol(rolUsuario);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado("activo");

        // Guardar en BD
        usuarioRepositorio.save(usuario);

        // Registrar intento por IP
        registrosPorIp.put(ip, registrosPorIp.get(ip) + 1);
        ultimoIntentoIp.put(ip, ahora);

        return "OK";
    }

    private boolean esContrasenaSegura(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) return false;
        boolean tieneLetra = contrasena.matches(".*[A-Za-z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");
        return tieneLetra && tieneNumero;
    }
}