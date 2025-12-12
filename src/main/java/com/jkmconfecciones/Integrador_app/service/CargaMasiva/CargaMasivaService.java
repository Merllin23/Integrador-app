package com.jkmconfecciones.Integrador_app.service.CargaMasiva;

import com.jkmconfecciones.Integrador_app.DTO.ProductoCargaMasivaDTO;
import com.jkmconfecciones.Integrador_app.DTO.ResultadoCargaMasivaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CargaMasivaService {

    ResultadoCargaMasivaDTO procesarArchivo(MultipartFile archivo) throws IOException;
    
    int importarProductos(List<ProductoCargaMasivaDTO> productos);
    
    byte[] generarPlantillaCSV();
}
