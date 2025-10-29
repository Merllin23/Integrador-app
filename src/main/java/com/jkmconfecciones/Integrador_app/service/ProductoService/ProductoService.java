package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Categoria;
import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {
    Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen);
    List<Producto> listarProductos();
    List<Producto> listarProductosPorColegio(Integer colegioId);
    void eliminarProducto(Integer id);
    Producto buscarPorId(Integer id);
    Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, org.springframework.web.multipart.MultipartFile imagen);
    List<Producto> listarProductosFiltrados(Integer colegioId, Integer categoriaId);
    List<Categoria> listarCategorias();
}
