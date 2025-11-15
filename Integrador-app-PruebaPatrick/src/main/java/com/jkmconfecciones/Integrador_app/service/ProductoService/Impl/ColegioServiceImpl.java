package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import com.jkmconfecciones.Integrador_app.repositorios.ColegioRepositorio;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ColegioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColegioServiceImpl implements ColegioService {

    private final ColegioRepositorio colegioRepository;

    @Override
    public List<Colegio> listarColegios() {
        return colegioRepository.findAll();
    }
}
