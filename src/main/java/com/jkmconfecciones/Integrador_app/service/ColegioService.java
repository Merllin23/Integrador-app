package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Colegio;

import java.util.List;

public interface ColegioService {
    List<Colegio> listarTodos();
    List<Colegio> buscarPorNombre(String termino);
    Colegio obtenerPorId(Long id);
}
