package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Talla;
import com.jkmconfecciones.Integrador_app.repositorios.TallaRepositorio;
import com.jkmconfecciones.Integrador_app.service.ProductoService.TallaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TallaServiceImpl implements TallaService {

    private final TallaRepositorio tallaRepositorio;

    @Override
    public List<Talla> listarTallas() {
        return tallaRepositorio.findAll();
    }

    @Override
    public Talla buscarPorId(Integer id) {
        return tallaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con ID: " + id));
    }

    @Override
    public Optional<Talla> obtenerPorNombre(String nombreTalla) {
        return tallaRepositorio.findByNombreTalla(nombreTalla);
    }
}
