-- ============================================
-- SCRIPT DE INSERCIÓN DE DATOS INICIALES
-- Base de datos: railway (MySQL en Railway)
-- Fecha: 5 de diciembre de 2025
-- ============================================

-- Desactivar verificación de claves foráneas temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. CATEGORÍAS (ordenadas por id)
-- ============================================
INSERT INTO `categoria` (`id_categoria`, `nombre`) VALUES
(1, 'Uniformes Escolares'),
(2, 'Uniformes Deportivos'),
(3, 'Accesorios'),
(4, 'Calzado Escolar')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ============================================
-- 2. COLECCIONES (ordenadas por id)
-- ============================================
INSERT INTO `coleccion` (`id_coleccion`, `nombre`) VALUES
(1, 'Colección Verano 2025'),
(2, 'Colección Invierno 2025'),
(3, 'Colección Deportiva'),
(4, 'Colección Formal')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ============================================
-- 3. TALLAS (ordenadas alfabéticamente)
-- ============================================
INSERT INTO `talla` (`id_talla`, `nombre_talla`) VALUES
(1, 'XS'),
(2, 'S'),
(3, 'M'),
(4, 'L'),
(5, 'XL'),
(6, 'XXL'),
(7, '4'),
(8, '6'),
(9, '8'),
(10, '10'),
(11, '12'),
(12, '14'),
(13, '16')
ON DUPLICATE KEY UPDATE nombre_talla = VALUES(nombre_talla);

-- ============================================
-- 4. COLEGIOS (ordenados alfabéticamente)
-- ============================================
INSERT INTO `colegio` (`id_colegio`, `nombre`, `logo_url`, `direccion`, `telefono`, `ruc`) VALUES
(1, 'Claretiano', 'EscudosColegios/Claret.jpg', NULL, NULL, NULL),
(2, 'Corazón de Oro', 'EscudosColegios/corazondeOro.jpg', NULL, NULL, NULL),
(3, 'Domingo Savio', 'EscudosColegios/domingoSavio.png', NULL, NULL, NULL),
(4, 'El Pilar', 'EscudosColegios/elPilar.jpg', NULL, NULL, NULL),
(5, 'Esclavistas', 'EscudosColegios/Esclavista.png', NULL, NULL, NULL),
(6, 'Fatima', 'EscudosColegios/Fatima.jpg', NULL, NULL, NULL),
(7, 'Internacional', 'EscudosColegios/Internacional.png', NULL, NULL, NULL),
(8, 'La Salle', 'EscudosColegios/laSalle.png', NULL, NULL, NULL),
(9, 'Max Ulhe', 'EscudosColegios/maxUlhe.png', NULL, NULL, NULL),
(10, 'Panamericano', 'EscudosColegios/Panamericano.jpg', NULL, NULL, NULL),
(11, 'Prescott', 'EscudosColegios/Prescott.png', NULL, NULL, NULL),
(12, 'San Jeronimo', 'EscudosColegios/sanJeronimo.jpg', NULL, NULL, NULL),
(13, 'Santiago Apostol', 'EscudosColegios/santiagoApostol.png', NULL, NULL, NULL),
(14, 'Santiago Salvador', 'EscudosColegios/santiagoSalvador.png', NULL, NULL, NULL),
(15, 'Santiago Vargas', 'EscudosColegios/santiagoVargas.jpg', NULL, NULL, NULL),
(16, 'Sonrisitas', 'EscudosColegios/Sonrisitas.jpg', NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE 
    nombre = VALUES(nombre),
    logo_url = VALUES(logo_url);

-- ============================================
-- 5. PROMOCIONES (ordenadas por id)
-- ============================================
INSERT INTO `promocion` (`id_promocion`, `nombre`, `descripcion`, `tipo_descuento`, `valor`, `fecha_inicio`, `fecha_fin`, `es_valida`) VALUES
(1, 'Descuento Escolar 2025', 'Descuento especial para el año escolar 2025', 'porcentaje', 15.00, '2025-10-19', '2026-01-19', 1),
(2, 'Oferta de Verano', 'Promoción especial para uniformes de verano', 'fijo', 20.00, '2025-10-19', '2025-12-19', 1)
ON DUPLICATE KEY UPDATE 
    nombre = VALUES(nombre),
    descripcion = VALUES(descripcion),
    tipo_descuento = VALUES(tipo_descuento),
    valor = VALUES(valor),
    fecha_inicio = VALUES(fecha_inicio),
    fecha_fin = VALUES(fecha_fin),
    es_valida = VALUES(es_valida);

-- Reactivar verificación de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================
SELECT 'Categorías insertadas:' as Tabla, COUNT(*) as Total FROM categoria;
SELECT 'Colecciones insertadas:' as Tabla, COUNT(*) as Total FROM coleccion;
SELECT 'Tallas insertadas:' as Tabla, COUNT(*) as Total FROM talla;
SELECT 'Colegios insertados:' as Tabla, COUNT(*) as Total FROM colegio;
SELECT 'Promociones insertadas:' as Tabla, COUNT(*) as Total FROM promocion;
