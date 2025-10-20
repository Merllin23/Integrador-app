package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import com.jkmconfecciones.Integrador_app.repositorios.ColegioRepository;
import com.jkmconfecciones.Integrador_app.service.ColegioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColegioServiceImpl implements ColegioService {

    @Autowired
    private ColegioRepository colegioRepository;

    @Override
    public List<Colegio> listarTodos() {
        return colegioRepository.findAll();
    }

    @Override
    public List<Colegio> buscarPorNombre(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodos();
        }
        return colegioRepository.findByNombreContainingIgnoreCase(termino.trim());
    }

    @Override
    public Colegio obtenerPorId(Long id) {
        return colegioRepository.findById(id).orElse(null);
    }
}
