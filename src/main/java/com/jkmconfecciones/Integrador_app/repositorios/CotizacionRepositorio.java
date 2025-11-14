package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CotizacionRepositorio extends JpaRepository<Cotizacion, Integer> {
    List<Cotizacion> findByUsuario(Usuario usuario);
}
