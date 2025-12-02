package com.jkmconfecciones.Integrador_app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para gestionar la carga y eliminación de imágenes en Cloudinary.
 * Cloudinary es un servicio CDN que almacena las imágenes en la nube.
 */
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Sube una imagen a Cloudinary y retorna la URL pública.
     *
     * @param imagen Archivo MultipartFile de la imagen
     * @param carpeta Nombre de la carpeta en Cloudinary (ej: "productos", "colegios")
     * @return URL pública de la imagen subida
     * @throws IOException Si hay error en la carga
     */
    public String subirImagen(MultipartFile imagen, String carpeta) throws IOException {
        if (imagen == null || imagen.isEmpty()) {
            throw new IllegalArgumentException("La imagen no puede estar vacía");
        }

        // Generar un public_id único para evitar colisiones
        String publicId = carpeta + "/" + UUID.randomUUID().toString();

        // Opciones de carga
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", carpeta,
                "resource_type", "auto",
                "overwrite", false,
                "transformation", new com.cloudinary.Transformation()
                        .quality("auto")
                        .fetchFormat("auto")
        );

        // Subir imagen
        Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), uploadParams);

        // Retornar la URL segura (HTTPS)
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Elimina una imagen de Cloudinary usando su URL.
     *
     * @param imageUrl URL de la imagen a eliminar
     * @throws IOException Si hay error en la eliminación
     */
    public void eliminarImagen(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return; // No hacer nada si no hay URL
        }

        // Extraer el public_id de la URL de Cloudinary
        // Ejemplo: https://res.cloudinary.com/demo/image/upload/v1234567890/productos/abc123.jpg
        // public_id sería: productos/abc123
        String publicId = extraerPublicId(imageUrl);

        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary.
     *
     * @param imageUrl URL completa de Cloudinary
     * @return public_id extraído o null si no es una URL de Cloudinary válida
     */
    private String extraerPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }

        try {
            // Formato típico: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }

            // Obtener la parte después de /upload/
            String afterUpload = parts[1];

            // Quitar la versión (v1234567890/)
            String withoutVersion = afterUpload.replaceFirst("v\\d+/", "");

            // Quitar la extensión del archivo
            int lastDotIndex = withoutVersion.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return withoutVersion.substring(0, lastDotIndex);
            }

            return withoutVersion;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica si una URL es de Cloudinary.
     *
     * @param imageUrl URL a verificar
     * @return true si es una URL de Cloudinary, false en caso contrario
     */
    public boolean esUrlCloudinary(String imageUrl) {
        return imageUrl != null && imageUrl.contains("cloudinary.com");
    }
}
