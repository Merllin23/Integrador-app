package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Producto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {
    Producto crearProducto(Producto producto, List<String> tallas, MultipartFile imagen);
}
