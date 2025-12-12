package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Categoria;
import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {
    Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen);
    void eliminarProducto(Integer id, HttpServletRequest request);
    Producto buscarPorId(Integer id);
    Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, org.springframework.web.multipart.MultipartFile imagen);
    List<Producto> listarProductosFiltrados(Integer colegioId, Integer categoriaId);
    List<Categoria> listarCategorias();

    // Métodos para inventario
    List<ProductoTalla> obtenerInventarioCompleto();
    List<ProductoTalla> obtenerInventarioPorColegio(Integer colegioId);
    void actualizarStock(Integer productoId, Integer cantidad, HttpServletRequest request);
    byte[] exportarInventarioExcel(Integer colegioId);
}
