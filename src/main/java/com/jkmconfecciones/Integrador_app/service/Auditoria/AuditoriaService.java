package com.jkmconfecciones.Integrador_app.service.Auditoria;

import com.jkmconfecciones.Integrador_app.entidades.AuditoriaSeguridad;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.AuditoriaRepositorio;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepositorio auditoriaRepositorio;

    //Registrar acción de auditoría

    @Transactional
    public void registrarAccion(Usuario usuario, String accion, String recurso, Long recursoId, 
                                String estado, String detalles, HttpServletRequest request) {
        String ipAddress = obtenerIpCliente(request);
        String userAgent = request.getHeader("User-Agent");

        AuditoriaSeguridad auditoria = AuditoriaSeguridad.builder()
                .usuario(usuario)
                .accion(accion)
                .recurso(recurso)
                .recursoId(recursoId)
                .ipAddress(ipAddress)
                .fechaHora(LocalDateTime.now())
                .estado(estado)
                .detalles(detalles)
                .userAgent(userAgent)
                .build();

        auditoriaRepositorio.save(auditoria);
        log.info("Auditoría registrada: {} - {} - {} por usuario {}", accion, recurso, estado, usuario.getCorreo());
    }

    //Registrar acción 
    @Transactional
    public void registrarAccionSimple(Usuario usuario, String accion, String recurso, String estado, String detalles) {
        AuditoriaSeguridad auditoria = AuditoriaSeguridad.builder()
                .usuario(usuario)
                .accion(accion)
                .recurso(recurso)
                .fechaHora(LocalDateTime.now())
                .estado(estado)
                .detalles(detalles)
                .build();

        auditoriaRepositorio.save(auditoria);
        log.info("Auditoría registrada: {} - {} - {}", accion, recurso, estado);
    }

    //Registrar login exitoso
    @Transactional
    public void registrarLogin(Usuario usuario, HttpServletRequest request) {
        registrarAccion(usuario, "LOGIN", "Sistema", null, "EXITOSO", 
                       "Inicio de sesión exitoso", request);
    }

    //Registrar login fallido
    @Transactional
    public void registrarLoginFallido(String correo, HttpServletRequest request) {
        String ipAddress = obtenerIpCliente(request);
        String userAgent = request.getHeader("User-Agent");

        AuditoriaSeguridad auditoria = AuditoriaSeguridad.builder()
                .accion("LOGIN")
                .recurso("Sistema")
                .ipAddress(ipAddress)
                .fechaHora(LocalDateTime.now())
                .estado("FALLIDO")
                .detalles("Intento de inicio de sesión fallido para: " + correo)
                .userAgent(userAgent)
                .build();

        auditoriaRepositorio.save(auditoria);
        log.warn("Login fallido para usuario: {}", correo);
    }

    //Registrar logout
    @Transactional
    public void registrarLogout(Usuario usuario, HttpServletRequest request) {
        registrarAccion(usuario, "LOGOUT", "Sistema", null, "EXITOSO", 
                       "Cierre de sesión", request);
    }

    //Obtener todos los registros con paginación
     
    public Page<AuditoriaSeguridad> obtenerTodos(Pageable pageable) {
        return auditoriaRepositorio.findAllByOrderByFechaHoraDesc(pageable);
    }

    //Buscar con filtros
     
    public Page<AuditoriaSeguridad> buscarConFiltros(String usuario, String accion, 
                                                      LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                                                      Pageable pageable) {
        return auditoriaRepositorio.buscarConFiltros(usuario, accion, fechaInicio, fechaFin, pageable);
    }

    //Obtener últimos 10 registros
     
    public List<AuditoriaSeguridad> obtenerUltimosRegistros() {
        return auditoriaRepositorio.findTop10ByOrderByFechaHoraDesc();
    }

    //Obtener registros de un usuario específico
     
    public Page<AuditoriaSeguridad> obtenerPorUsuario(Usuario usuario, Pageable pageable) {
        return auditoriaRepositorio.findByUsuarioOrderByFechaHoraDesc(usuario, pageable);
    }

    //Obtener IP del cliente

    private String obtenerIpCliente(HttpServletRequest request) {

        String[] cabeceras = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : cabeceras) {
            String ip = request.getHeader(header);

            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {

                // Si hay varias IPs separadas por coma → tomar la primera
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                }

                return ip;
            }
        }

        return request.getRemoteAddr();
    }

}
