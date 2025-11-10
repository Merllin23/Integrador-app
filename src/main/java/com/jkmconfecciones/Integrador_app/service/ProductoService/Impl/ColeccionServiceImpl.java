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
}
