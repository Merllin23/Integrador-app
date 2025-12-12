package com.jkmconfecciones.Integrador_app.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoRepositorio;

@Component
@RequiredArgsConstructor
public class MetricsCustom {

    private final ProductoRepositorio productoRepositorio;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void initMetrics() {
        // Métrica: cantidad de productos totales
        meterRegistry.gauge("productos.totales", productoRepositorio, repo -> repo.count());

        // Métrica: productos por categoría (ejemplo simple)
        meterRegistry.gauge("productos.categoria.1", productoRepositorio,
                repo -> repo.findByCategoriaId(1).size());

        // Métrica: productos por colegio
        meterRegistry.gauge("productos.colegio.1", productoRepositorio,
                repo -> repo.findByColegioId(1).size());
    }
}

