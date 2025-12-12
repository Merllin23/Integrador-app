package com.jkmconfecciones.Integrador_app.service.Tallas.Impl;

import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoTallaRepositorio;
import com.jkmconfecciones.Integrador_app.service.Tallas.ProductoTallaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoTallaServiceImpl implements ProductoTallaService {

    private final ProductoTallaRepositorio productoTallaRepositorio;

    public ProductoTallaServiceImpl(ProductoTallaRepositorio productoTallaRepositorio) {
        this.productoTallaRepositorio = productoTallaRepositorio;
    }

    @Override
    public List<ProductoTalla> listarTodos() {
        return productoTallaRepositorio.findAllWithDetails();
    }

    @Override
    public List<Map<String, Object>> listarTallasComoMap() {
        return productoTallaRepositorio.findAllWithDetails().stream().map(pt -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pt.getId());
            map.put("productoId", pt.getProducto().getId());
            map.put("productoNombre", pt.getProducto().getNombre());
            map.put("productoPrecioBase", pt.getProducto().getPrecioBase());
            map.put("productoimagenUrl", pt.getProducto().getImagenUrl());
            map.put("tallaId", pt.getTalla().getId());
            map.put("tallaNombre", pt.getTalla().getNombreTalla());
            map.put("cantidadStock", pt.getCantidadStock());
            map.put("precioUnitarioFinal", pt.getPrecioUnitarioFinal());
            map.put("activo", pt.getActivo() != null ? pt.getActivo() : true);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> obtenerDetalle(Long id) {
        ProductoTalla pt = productoTallaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoTalla no encontrado"));

        Map<String, Object> map = new HashMap<>();
        map.put("id", pt.getId());
        map.put("productoNombre", pt.getProducto().getNombre());
        map.put("tallaNombre", pt.getTalla().getNombreTalla());
        map.put("cantidadStock", pt.getCantidadStock());
        map.put("precioUnitarioFinal", pt.getPrecioUnitarioFinal());
        map.put("activo", pt.getActivo() != null ? pt.getActivo() : true);
        return map;
    }

    @Override
    public Map<String, Object> toggleEstado(Long id, Boolean nuevoEstado) {
        ProductoTalla pt = productoTallaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoTalla no encontrado"));

        pt.setActivo(nuevoEstado);
        productoTallaRepositorio.save(pt);

        Map<String, Object> response = new HashMap<>();
        response.put("id", pt.getId());
        response.put("activo", pt.getActivo());
        response.put("mensaje", "Estado actualizado correctamente");
        return response;
    }
}
