package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Coleccion;
import java.util.List;

public interface ColeccionService {
    List<Coleccion> listarColecciones();

    Coleccion guardarColeccion(Coleccion coleccion);

    void editarColeccion(Long id, String nombre);
    
    void eliminarColeccion(Long id);
}
