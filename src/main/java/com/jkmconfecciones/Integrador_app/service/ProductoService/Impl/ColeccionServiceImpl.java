package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Coleccion;
import com.jkmconfecciones.Integrador_app.repositorios.ColeccionRepositorio;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionServiceImpl implements ColeccionService {

    private final ColeccionRepositorio coleccionRepository;

    @Override
    public List<Coleccion> listarColecciones() {
        return coleccionRepository.findAll();
    }

    @Override
    public Coleccion guardarColeccion(Coleccion coleccion) {
        return coleccionRepository.save(coleccion);
    }

    @Override
    public void editarColeccion(Long id, String nombre) {
        Coleccion c = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));

        c.setNombre(nombre);
        coleccionRepository.save(c);
    }

    @Override
    public void eliminarColeccion(Long id) {
        coleccionRepository.deleteById(id);
    }

}
