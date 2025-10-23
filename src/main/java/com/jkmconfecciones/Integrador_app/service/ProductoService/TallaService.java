package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Talla;

import java.util.List;
import java.util.Optional;

public interface TallaService {

    List<Talla> listarTallas();
    Talla buscarPorId(Integer id);
    Optional<Talla> obtenerPorNombre(String nombreTalla);
}
