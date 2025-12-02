package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Promocion;
import com.jkmconfecciones.Integrador_app.repositorios.PromocionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromocionService {

    private final PromocionRepositorio promocionRepositorio;

    public List<Promocion> listarTodas() {
        return promocionRepositorio.findAll();
    }

    public List<Promocion> listarPorColegio(Integer colegioId) {
        return promocionRepositorio.findByColegioId(colegioId);
    }

    public List<Promocion> listarPromocionesActivas() {
        return promocionRepositorio.findPromocionesActivas(LocalDate.now());
    }

    public List<Promocion> listarPromocionesActivasPorColegio(Integer colegioId) {
        return promocionRepositorio.findPromocionesActivasPorColegio(colegioId, LocalDate.now());
    }

    public List<Promocion> listarPromocionesDelProducto(Integer productoId) {
        return promocionRepositorio.findPromocionesDelProducto(productoId);
    }

    public List<Promocion> listarPromocionesporDia(Integer colegioId, Promocion.DiaSemana dia) {
        return promocionRepositorio.findPromocionesporDiaYColegio(colegioId, dia);
    }

    public Optional<Promocion> obtenerPorId(Integer id) {
        return promocionRepositorio.findById(id);
    }

    public Promocion crear(Promocion promocion) {
        return promocionRepositorio.save(promocion);
    }

    public Promocion actualizar(Integer id, Promocion promocionActualizada) {
        return promocionRepositorio.findById(id).map(promocion -> {
            promocion.setNombre(promocionActualizada.getNombre());
            promocion.setDescripcion(promocionActualizada.getDescripcion());
            promocion.setColegio(promocionActualizada.getColegio());
            promocion.setDiaSemana(promocionActualizada.getDiaSemana());
            promocion.setDescuentoPorcentaje(promocionActualizada.getDescuentoPorcentaje());
            promocion.setFechaInicio(promocionActualizada.getFechaInicio());
            promocion.setFechaFin(promocionActualizada.getFechaFin());
            promocion.setEsValida(promocionActualizada.getEsValida());
            return promocionRepositorio.save(promocion);
        }).orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
    }

    public void toggleActivo(Integer id) {
        promocionRepositorio.findById(id).ifPresent(promocion -> {
            promocion.setEsValida(!promocion.getEsValida());
            promocionRepositorio.save(promocion);
        });
    }

    public void eliminar(Integer id) {
        promocionRepositorio.deleteById(id);
    }

    // Obtener descuento disponible para un producto en un colegio
    public Double obtenerDescuentoProducto(Integer productoId, Integer colegioId) {
        List<Promocion> promociones = promocionRepositorio.findPromocionesActivasPorColegio(colegioId, LocalDate.now());
        return promociones.stream()
                .filter(p -> p.getProductos() != null && p.getProductos().stream()
                        .anyMatch(prod -> prod.getId().equals(productoId)))
                .mapToDouble(Promocion::getDescuentoPorcentaje)
                .max()
                .orElse(0.0);
    }
}
