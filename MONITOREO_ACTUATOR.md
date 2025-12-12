# 🚀 Nuevas Funcionalidades de Monitoreo - JKM Confecciones

## 📋 Resumen de Integracion

Se han integrado exitosamente las siguientes funcionalidades de monitoreo y mantenimiento, **adaptadas para Railway**:

### ✅ Archivos Creados/Modificados

1. **CloudinaryHealthIndicator.java** (NUEVO)
   - Ruta: `src/main/java/com/jkmconfecciones/Integrador_app/config/`
   - Health check para validar conexión con Cloudinary
   - Reemplaza el check de carpeta local por validación en la nube

2. **MetricsCustom.java** (NUEVO)
   - Ruta: `src/main/java/com/jkmconfecciones/Integrador_app/config/`
   - Métricas personalizadas de productos, categorías y colegios
   - Se expone en `/actuator/metrics`

3. **MantenimientoProgramadoService.java** (NUEVO)
   - Ruta: `src/main/java/com/jkmconfecciones/Integrador_app/service/`
   - Tareas programadas adaptadas para Railway (sin rutas Windows)
   - **Deshabilitado por defecto** - se activa con propiedad

4. **ProductoRepositorio.java** (MODIFICADO)
   - Agregados métodos: `findByCategoriaId()` y `existsByImagenUrl()`
   - Necesarios para métricas y limpieza

5. **SeguridadConfig.java** (MODIFICADO)
   - Agregada protección: `.requestMatchers("/actuator/**").hasRole("ADMINISTRADOR")`
   - Solo administradores pueden ver métricas

6. **application.properties** (MODIFICADO)
   - Configuración expandida de Actuator
   - Endpoints: health, metrics, info, env

---

## 🔍 Endpoints de Monitoreo Disponibles

### 1️⃣ Health Check
**URL**: `https://integrador-app-production-xxxx.up.railway.app/actuator/health`
- **Acceso**: Solo ADMINISTRADOR
- **Muestra**: Estado de Cloudinary, MySQL, JVM

**Ejemplo de respuesta**:
```json
{
  "status": "UP",
  "components": {
    "cloudinary": {
      "status": "UP",
      "details": {
        "cloud_name": "dhaidbkmt",
        "status": "conectado"
      }
    },
    "db": {
      "status": "UP"
    }
  }
}
```

### 2️⃣ Métricas
**URL**: `https://integrador-app-production-xxxx.up.railway.app/actuator/metrics`
- **Acceso**: Solo ADMINISTRADOR
- **Lista todas las métricas disponibles**

**Métricas personalizadas disponibles**:
- `productos.totales` - Total de productos
- `productos.categoria.1` - Productos en categoría 1
- `productos.categoria.2` - Productos en categoría 2
- `productos.colegio.1` - Productos del colegio 1
- `productos.colegio.2` - Productos del colegio 2

**Ejemplo consulta específica**:
```
GET /actuator/metrics/productos.totales
```

### 3️⃣ Información de la Aplicación
**URL**: `https://integrador-app-production-xxxx.up.railway.app/actuator/info`
- **Acceso**: Solo ADMINISTRADOR
- **Muestra**: Versión, nombre, Java version

### 4️⃣ Variables de Entorno
**URL**: `https://integrador-app-production-xxxx.up.railway.app/actuator/env`
- **Acceso**: Solo ADMINISTRADOR
- **Muestra**: Propiedades de configuración (valores sensibles ocultos)

---

## ⚙️ Tareas Programadas

El servicio `MantenimientoProgramadoService` está **DESHABILITADO por defecto** en Railway.

### Para Habilitarlo (Solo Local):

Agregar en `application.properties`:
```properties
mantenimiento.programado.habilitado=true
```

### Tareas Configuradas:

| Tarea | Frecuencia | Hora | Descripción |
|-------|-----------|------|-------------|
| Reporte de Inventario | Semanal (Lunes) | 3:00 AM | Genera logs con estadísticas |
| Limpieza de Logs | Semanal (Domingo) | 2:00 AM | Limpia logs antiguos |
| Monitoreo de Inventario | Diario | 6:00 AM | Alerta si hay 0 productos |
| Estadísticas Diarias | Diario | 11:59 PM | Resumen del día |

**⚠️ IMPORTANTE**: Estas tareas usan timezone `America/Lima` (GMT-5).

---

## 🧪 Cómo Probar en Railway

### 1. Desplegar cambios:
```bash
git add .
git commit -m "feat: agregado monitoreo con Actuator y métricas personalizadas"
git push origin main
```

### 2. Esperar que Railway redespliegue

### 3. Probar endpoints (como ADMINISTRADOR):

#### Opción A: Navegador
1. Iniciar sesión como ADMINISTRADOR en la app
2. Ir a: `https://tu-app.railway.app/actuator/health`

#### Opción B: cURL
```bash
# Primero obtener cookie de sesión
curl -c cookies.txt -X POST https://tu-app.railway.app/procesarLogin \
  -d "username=admin@jkm.com&password=tupassword"

# Luego consultar health
curl -b cookies.txt https://tu-app.railway.app/actuator/health
```

#### Opción C: Postman
1. Hacer POST a `/procesarLogin` con credenciales de admin
2. Guardar cookie `JSESSIONID`
3. Hacer GET a `/actuator/health` con la cookie

---

## 📊 Métricas JVM Automáticas

Además de las métricas personalizadas, Spring Boot Actuator expone:

- `jvm.memory.used` - Memoria usada
- `jvm.memory.max` - Memoria máxima
- `jvm.threads.live` - Threads activos
- `system.cpu.usage` - Uso de CPU
- `http.server.requests` - Requests HTTP por endpoint

---

## 🔐 Seguridad

✅ Todos los endpoints `/actuator/**` están protegidos
✅ Solo usuarios con rol `ADMINISTRADOR` pueden acceder
✅ Usuarios normales y visitantes reciben **403 Forbidden**

---

## 🐛 Troubleshooting

### Error: "404 Not Found" en /actuator/health
- **Causa**: No estás autenticado como administrador
- **Solución**: Iniciar sesión con cuenta ADMIN primero

### Error: "403 Forbidden"
- **Causa**: Tu usuario no tiene rol ADMINISTRADOR
- **Solución**: Cambiar rol en base de datos o usar cuenta admin

### Métricas muestran 0
- **Causa**: Puede ser normal si no hay datos en esa categoría/colegio
- **Verificar**: `productos.totales` debe mostrar el total correcto

### Health de Cloudinary DOWN
- **Causa**: Credenciales mal configuradas en Railway
- **Verificar**: Variables `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`

---

## 📝 Diferencias con los Archivos Originales

| Aspecto | Archivos Originales | Versión Adaptada |
|---------|-------------------|------------------|
| **Health Check** | Carpeta local `C:/jkm/productos/` | Cloudinary API |
| **Rutas** | Hardcoded Windows | Variables de entorno |
| **Backups** | Scripts .bat | Logs en Railway |
| **Limpieza** | Archivos físicos | Deshabilitado en Railway |
| **Timezone** | No especificado | America/Lima |

---

## ✅ Verificación de Funcionalidad

Ejecuta este checklist después de desplegar:

- [ ] La app arranca correctamente
- [ ] Login funciona normal
- [ ] `/actuator/health` responde con status UP
- [ ] `/actuator/metrics` lista las métricas
- [ ] Métricas personalizadas aparecen (`productos.totales`)
- [ ] Health de Cloudinary muestra `cloud_name` correcto
- [ ] Usuarios no-admin reciben 403 en `/actuator/**`

---

## 🚨 IMPORTANTE

1. **NO habilitar** `mantenimiento.programado.habilitado=true` en Railway (no es necesario)
2. Los endpoints de Actuator consumen recursos - solo úsalos para monitoreo, no para dashboards públicos
3. Railway puede reiniciar el contenedor periódicamente - esto es normal

---

## 📞 Soporte

Si encuentras problemas:
1. Verificar logs de Railway: `railway logs`
2. Revisar variables de entorno en Railway dashboard
3. Confirmar que el rol ADMINISTRADOR existe en BD

---

**Fecha de integración**: 12 de diciembre de 2025
**Versión**: 1.0.0
**Estado**: ✅ Listo para producción
