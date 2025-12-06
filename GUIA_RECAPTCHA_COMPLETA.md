# 🔐 Configuración de reCAPTCHA - Guía Completa

## 📋 Índice
1. [Crear cuenta y obtener claves](#1-crear-cuenta-y-obtener-claves)
2. [Configurar en tu proyecto](#2-configurar-en-tu-proyecto)
3. [Configurar en Railway](#3-configurar-en-railway)
4. [Usar en tus controladores](#4-usar-en-tus-controladores)
5. [Frontend - HTML](#5-frontend---html)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. Crear cuenta y obtener claves

### Paso 1: Ir a Google reCAPTCHA Admin Console
Ve a: https://www.google.com/recaptcha/admin

### Paso 2: Iniciar sesión
- Usa tu cuenta de Google
- Si no tienes, crea una gratis

### Paso 3: Registrar un nuevo sitio
1. Click en el botón **"+"** (Agregar)
2. Llena el formulario:

```
Label (Etiqueta): JKM Confecciones
```

3. **Tipo de reCAPTCHA**: Selecciona **reCAPTCHA v2**
   - ✅ Marca: "Casilla de verificación 'No soy un robot'"

4. **Dominios autorizados**:
   ```
   localhost
   tu-app.up.railway.app
   tudominio.com (si tienes uno personalizado)
   ```
   ⚠️ **IMPORTANTE**: 
   - No incluyas `http://` o `https://`
   - Solo el dominio puro
   - Agrega uno por línea

5. Acepta los términos de servicio
6. Click en **"Enviar"**

### Paso 4: Copiar tus claves
Después de crear el sitio verás:

```
🔑 CLAVE DEL SITIO (Site Key):
6LeXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

🔐 CLAVE SECRETA (Secret Key):
6LeYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
```

**GUARDA ESTAS CLAVES** - Las necesitarás para configurar tu aplicación.

---

## 2. Configurar en tu proyecto

### Archivo: `src/main/resources/application.properties`

Ya está configurado con:

```properties
# reCAPTCHA Configuration
recaptcha.secret.key=${RECAPTCHA_SECRET:6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe}
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify
```

**Explicación**:
- `${RECAPTCHA_SECRET:...}` = Lee la variable de entorno `RECAPTCHA_SECRET`
- Si no existe, usa el valor por defecto (clave de prueba de Google)
- La clave de prueba `6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe` **SIEMPRE aprueba** (solo para desarrollo)

### Para desarrollo local (opcional)

Crea un archivo `.env` en la raíz del proyecto:

```env
RECAPTCHA_SECRET=tu_clave_secreta_aqui
RECAPTCHA_SITE_KEY=tu_clave_sitio_aqui
```

---

## 3. Configurar en Railway

### Método 1: Por interfaz web (RECOMENDADO)

1. Ve a tu proyecto en Railway: https://railway.app
2. Selecciona tu servicio
3. Ve a la pestaña **"Variables"**
4. Click en **"+ New Variable"**
5. Agrega:

```
Variable: RECAPTCHA_SECRET
Value: 6LeYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
```

6. Click en **"Add"**
7. Railway reiniciará automáticamente tu app

### Método 2: Por Railway CLI

```bash
railway variables set RECAPTCHA_SECRET=tu_clave_secreta_aqui
```

### Método 3: Archivo railway.toml (ya configurado)

El archivo `railway.toml` en tu proyecto ya tiene:

```toml
[build]
builder = "nixpacks"

[deploy]
startCommand = "java -jar target/Integrador-app-0.0.1-SNAPSHOT.jar"
restartPolicyType = "on-failure"
restartPolicyMaxRetries = 3
```

---

## 4. Usar en tus controladores

### Ejemplo: Controlador de Registro

```java
package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.service.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Obtener IP del cliente
        String clientIp = obtenerIpCliente(request);

        // 🔐 VALIDAR RECAPTCHA
        boolean captchaValido = recaptchaService.verificarCaptcha(recaptchaResponse, clientIp);

        if (!captchaValido) {
            redirectAttributes.addFlashAttribute("error", "Por favor, completa el reCAPTCHA");
            return "redirect:/registro";
        }

        // ✅ Si el captcha es válido, continuar con el registro
        // ... tu lógica de registro aquí ...

        redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente");
        return "redirect:/login";
    }

    // Método para obtener la IP real del cliente (importante para Railway/Nginx)
    private String obtenerIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Si hay múltiples IPs (proxies), tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
```

---

## 5. Frontend - HTML

### Ejemplo: Formulario de Registro

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Registro - JKM Confecciones</title>
    
    <!-- 🔐 CARGAR SCRIPT DE RECAPTCHA -->
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body>
    <form method="POST" action="/registro">
        
        <!-- Tus campos del formulario -->
        <input type="text" name="nombre" placeholder="Nombre" required>
        <input type="email" name="correo" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>

        <!-- 🔐 WIDGET DE RECAPTCHA -->
        <div class="g-recaptcha" 
             data-sitekey="TU_CLAVE_SITIO_AQUI"
             data-theme="light"
             data-size="normal">
        </div>

        <button type="submit">Registrarse</button>
    </form>

    <!-- Mostrar errores -->
    <div th:if="${error}" class="alert alert-danger">
        <span th:text="${error}"></span>
    </div>
</body>
</html>
```

### Opciones de personalización del widget:

```html
<!-- TEMA CLARO -->
<div class="g-recaptcha" 
     data-sitekey="tu_clave"
     data-theme="light">
</div>

<!-- TEMA OSCURO -->
<div class="g-recaptcha" 
     data-sitekey="tu_clave"
     data-theme="dark">
</div>

<!-- TAMAÑO COMPACTO -->
<div class="g-recaptcha" 
     data-sitekey="tu_clave"
     data-size="compact">
</div>

<!-- CALLBACK PERSONALIZADO -->
<div class="g-recaptcha" 
     data-sitekey="tu_clave"
     data-callback="onCaptchaSuccess">
</div>

<script>
function onCaptchaSuccess(token) {
    console.log('✅ reCAPTCHA completado:', token);
}
</script>
```

---

## 6. Troubleshooting

### ❌ Error: "Invalid site key"
**Causa**: La clave del sitio en el HTML no coincide con la registrada en Google.
**Solución**: Verifica que estés usando la **Site Key** correcta en el HTML.

### ❌ Error: "Timeout or duplicate"
**Causa**: El token de reCAPTCHA expiró (son válidos por 2 minutos).
**Solución**: El usuario debe completar el captcha nuevamente.

### ❌ El captcha no se muestra
**Causa**: El script de Google no se cargó correctamente.
**Solución**: 
1. Verifica tu conexión a internet
2. Asegúrate de incluir: `<script src="https://www.google.com/recaptcha/api.js" async defer></script>`
3. Revisa la consola del navegador (F12) para errores

### ❌ Error: "Missing required parameter: secret"
**Causa**: La variable de entorno `RECAPTCHA_SECRET` no está configurada en Railway.
**Solución**: Configura la variable en Railway (ver paso 3).

### ❌ Siempre falla en producción pero funciona en local
**Causa**: El dominio de Railway no está registrado en Google reCAPTCHA.
**Solución**: 
1. Ve a https://www.google.com/recaptcha/admin
2. Edita tu sitio
3. Agrega el dominio de Railway (ejemplo: `tu-app.up.railway.app`)

### ✅ Verificar que funciona correctamente

1. **Desarrollo local**: Usa las claves de prueba de Google
   ```
   Site Key: 6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
   Secret Key: 6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
   ```
   Estas claves **SIEMPRE aprueban** el captcha (solo para testing).

2. **Producción**: Usa tus claves reales de Google.

3. **Ver logs**: En Railway, ve a "Deployments" → Click en tu deployment → "View Logs"
   Busca mensajes como:
   ```
   ✅ reCAPTCHA verificado exitosamente
   ❌ reCAPTCHA inválido
   ```

---

## 📝 Checklist de Implementación

- [ ] Registrar sitio en Google reCAPTCHA Console
- [ ] Copiar Site Key y Secret Key
- [ ] Agregar dominios (localhost + Railway)
- [ ] Configurar `RECAPTCHA_SECRET` en Railway
- [ ] Actualizar `data-sitekey` en tus formularios HTML
- [ ] Agregar validación en controladores con `RecaptchaService`
- [ ] Probar en desarrollo local
- [ ] Hacer deploy a Railway
- [ ] Probar en producción

---

## 🎯 Resumen Rápido

### Para desarrollo local (testing):
```properties
# application.properties (ya configurado)
recaptcha.secret.key=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
```

```html
<!-- HTML -->
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<div class="g-recaptcha" data-sitekey="6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI"></div>
```

### Para Railway (producción):
```bash
# Variable de entorno en Railway
RECAPTCHA_SECRET=tu_clave_secreta_real
```

```html
<!-- HTML -->
<div class="g-recaptcha" data-sitekey="tu_clave_sitio_real"></div>
```

---

## 🔗 Enlaces Útiles

- Google reCAPTCHA Admin: https://www.google.com/recaptcha/admin
- Documentación oficial: https://developers.google.com/recaptcha
- Railway Docs: https://docs.railway.app
- Testing Keys: https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do

---

**✅ Servicio `RecaptchaService` ya creado y listo para usar en `src/main/java/com/jkmconfecciones/Integrador_app/service/RecaptchaService.java`**
