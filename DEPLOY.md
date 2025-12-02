# 🚀 Guía de Despliegue - JKM Confecciones en Railway

Esta guía te llevará paso a paso para desplegar tu aplicación Spring Boot en Railway con MySQL y Cloudinary.

---

## 📋 **Pre-requisitos**

Antes de comenzar, asegúrate de tener:

- ✅ Cuenta en [Railway](https://railway.app/) (puedes usar GitHub para login)
- ✅ Cuenta en [Cloudinary](https://cloudinary.com/) (gratis)
- ✅ Repositorio en GitHub con la rama `deploy`
- ✅ Cuenta de Gmail configurada para envío de emails

---

## 🎯 **PASO 1: Configurar Cloudinary**

### 1.1 Crear cuenta y obtener credenciales

1. Ve a [https://cloudinary.com/users/register/free](https://cloudinary.com/users/register/free)
2. Regístrate (gratis - 25GB de almacenamiento)
3. Una vez dentro, ve al **Dashboard**
4. Anota estos 3 valores (los necesitarás después):
   - `Cloud Name`
   - `API Key`
   - `API Secret`

### 1.2 Crear carpetas en Cloudinary (Opcional)

Cloudinary creará las carpetas automáticamente, pero si quieres organizarlas:
- Ve a **Media Library**
- Crea una carpeta llamada `productos`

---

## 🎯 **PASO 2: Preparar el repositorio en GitHub**

### 2.1 Hacer commit y push de los cambios

Desde tu terminal en VS Code:

```bash
# Verificar que estás en la rama deploy
git branch

# Agregar todos los archivos nuevos
git add .

# Hacer commit
git commit -m "feat: Configurar despliegue Railway con Cloudinary"

# Push a GitHub
git push origin deploy
```

### 2.2 Verificar en GitHub

Ve a tu repositorio en GitHub y verifica que la rama `deploy` tenga estos archivos nuevos:
- ✅ `Dockerfile`
- ✅ `.dockerignore`
- ✅ `railway.toml`
- ✅ Cambios en `pom.xml`
- ✅ Nuevo archivo `CloudinaryService.java`

---

## 🎯 **PASO 3: Crear proyecto en Railway**

### 3.1 Crear nuevo proyecto

1. Ve a [https://railway.app/](https://railway.app/)
2. Haz clic en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Autoriza a Railway para acceder a tu GitHub (si es primera vez)
5. Selecciona el repositorio: **`Integrador-app`**

### 3.2 Configurar la rama de despliegue

1. Una vez creado el servicio, haz clic en el servicio creado
2. Ve a **Settings** (⚙️)
3. En la sección **"Source"**, busca **"Branch"**
4. Cambia de `main` a **`deploy`** ⚠️ IMPORTANTE
5. Guarda los cambios

---

## 🎯 **PASO 4: Agregar base de datos MySQL**

### 4.1 Crear servicio MySQL

1. En tu proyecto de Railway, haz clic en **"+ New"**
2. Selecciona **"Database"** → **"Add MySQL"**
3. Railway creará automáticamente la base de datos

### 4.2 Obtener credenciales de MySQL

1. Haz clic en el servicio **MySQL** que acabas de crear
2. Ve a la pestaña **"Variables"**
3. Verás variables como:
   - `MYSQL_URL`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `MYSQL_DATABASE`
   - `MYSQL_HOST`
   - `MYSQL_PORT`

**NO necesitas copiarlas manualmente**, las usaremos en el siguiente paso.

---

## 🎯 **PASO 5: Configurar variables de entorno**

### 5.1 Ir al servicio de la aplicación

1. En Railway, haz clic en tu servicio de aplicación (el que NO es MySQL)
2. Ve a la pestaña **"Variables"**
3. Haz clic en **"+ New Variable"** o **"Raw Editor"**

### 5.2 Agregar todas las variables

Copia y pega esto en el **Raw Editor**, reemplazando los valores entre `< >`:

```env
# Base de datos MySQL (Railway la conectará automáticamente)
SPRING_DATASOURCE_URL=${MYSQL_URL}
DB_USER=${MYSQL_USER}
DB_PASSWORD=${MYSQL_PASSWORD}

# Cloudinary - Usa estos valores específicos
CLOUDINARY_CLOUD_NAME=dhaidbkmt
CLOUDINARY_API_KEY=153294565964528
CLOUDINARY_API_SECRET=cBeMxTF66TdnE1OSj5dQffcMNZI

# Gmail (reemplaza con tu email y contraseña de aplicación)
MAIL_USER=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña-de-16-caracteres

# reCAPTCHA (reemplaza con tu clave secreta)
RECAPTCHA_SECRET=tu-secret-key-recaptcha

# Puerto (Railway lo asigna automáticamente)
PORT=8080
```

### 5.3 Configurar application.properties para Railway

Crea un nuevo archivo `src/main/resources/application-prod.properties`:

```properties
spring.application.name=Integrador-app

# Conexión a MySQL (usa variables de entorno)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Thymeleaf
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=true

# Cloudinary
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USER}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# reCAPTCHA
recaptcha.secret.key=${RECAPTCHA_SECRET}
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify

# Puerto (Railway asigna dinámicamente)
server.port=${PORT:8080}
```

**Luego haz commit y push:**

```bash
git add src/main/resources/application-prod.properties
git commit -m "feat: Agregar configuración de producción"
git push origin deploy
```

---

## 🎯 **PASO 6: Vincular MySQL con tu aplicación**

### 6.1 Crear referencia a MySQL

1. En tu servicio de aplicación (NO MySQL), ve a **"Settings"**
2. Busca la sección **"Service Variables"**
3. Haz clic en **"+ New Variable"** → **"Add Reference"**
4. Selecciona las siguientes variables del servicio MySQL:
   - `MYSQL_URL` → Nombrarla como `SPRING_DATASOURCE_URL`
   - `MYSQL_USER` → Nombrarla como `DB_USER`
   - `MYSQL_PASSWORD` → Nombrarla como `DB_PASSWORD`

Esto conectará automáticamente tu app con la base de datos.

---

## 🎯 **PASO 7: Obtener contraseña de aplicación de Gmail**

Si no tienes una "Contraseña de aplicación" de Gmail:

1. Ve a [https://myaccount.google.com/security](https://myaccount.google.com/security)
2. Activa **"Verificación en dos pasos"** (si no la tienes)
3. Busca **"Contraseñas de aplicaciones"**
4. Genera una nueva para "Correo" y "Otro dispositivo"
5. Copia la contraseña (16 caracteres sin espacios)
6. Úsala en la variable `MAIL_PASSWORD`

---

## 🎯 **PASO 8: Desplegar**

### 8.1 Trigger manual del despliegue

1. Ve a la pestaña **"Deployments"** de tu servicio
2. Haz clic en **"Deploy"** o simplemente espera
3. Railway detectará el `Dockerfile` y comenzará el build

### 8.2 Monitorear el despliegue

Observa los logs en tiempo real:
- ✅ **Build Stage**: Compilación con Maven
- ✅ **Deploy Stage**: Ejecución del contenedor
- ✅ **Healthcheck**: Verificación de `/actuator/health`

El despliegue puede tomar **5-10 minutos** la primera vez.

---

## 🎯 **PASO 9: Verificar el despliegue**

### 9.1 Obtener la URL pública

1. En la pestaña **"Settings"** de tu servicio
2. Busca **"Domains"**
3. Haz clic en **"Generate Domain"**
4. Railway te dará una URL como: `https://integrador-app-production.up.railway.app`

### 9.2 Probar la aplicación

Abre la URL en tu navegador:
- ✅ Debería cargar la landing page
- ✅ Prueba el login/registro
- ✅ Sube una imagen de producto (se guardará en Cloudinary)

---

## 🎯 **PASO 10: Migrar imágenes existentes (Opcional)**

Si tienes imágenes en `C:\jkm\productos\`, debes subirlas manualmente a Cloudinary:

### Opción A: Upload manual
1. Ve a Cloudinary → Media Library
2. Crea la carpeta `productos`
3. Sube las imágenes una por una

### Opción B: Usando Cloudinary CLI (avanzado)
```bash
npm install -g cloudinary-cli
cloudinary config
cloudinary upload C:\jkm\productos\* productos/
```

Luego actualiza las URLs en la base de datos.

---

## 📊 **Monitoreo y Logs**

### Ver logs en tiempo real
1. En Railway, ve a tu servicio
2. Pestaña **"Deployments"**
3. Haz clic en el deployment activo
4. Verás los logs de Spring Boot

### Verificar salud de la app
Accede a: `https://tu-app.up.railway.app/actuator/health`

---

## ⚠️ **Solución de problemas comunes**

### Error: "Connection refused" a MySQL
- Verifica que las variables `MYSQL_URL`, `DB_USER`, `DB_PASSWORD` estén configuradas
- Verifica que el servicio MySQL esté corriendo

### Error: "Cloudinary credentials not found"
- Verifica que las 3 variables de Cloudinary estén configuradas correctamente
- NO incluyas espacios en las credenciales

### Error: "Failed to send email"
- Verifica que `MAIL_USER` y `MAIL_PASSWORD` sean correctos
- La contraseña debe ser de aplicación, NO tu contraseña de Gmail normal

### Build exitoso pero app no responde
- Verifica que el puerto `8080` esté configurado
- Revisa los logs de despliegue en Railway

---

## 🎉 **¡Listo!**

Tu aplicación JKM Confecciones debería estar corriendo en Railway con:
- ✅ Base de datos MySQL persistente
- ✅ Imágenes almacenadas en Cloudinary (CDN global)
- ✅ Deploy automático desde GitHub (rama `deploy`)
- ✅ Variables de entorno seguras

### Próximos pasos sugeridos:
1. Configura un dominio personalizado en Railway (opcional)
2. Habilita HTTPS (Railway lo hace automáticamente)
3. Configura backups de la base de datos
4. Monitorea el uso de créditos en Railway

---

## 📝 **Comandos útiles**

```bash
# Ver estado del repositorio
git status

# Hacer cambios y redesplegar
git add .
git commit -m "fix: Descripción del cambio"
git push origin deploy

# Cambiar entre ramas
git checkout main
git checkout deploy

# Merge de deploy a main (cuando todo funcione)
git checkout main
git merge deploy
git push origin main
```

---

## 💰 **Costos y límites**

### Railway (Plan Trial)
- ✅ $5 de crédito inicial
- ✅ 500 horas de ejecución/mes
- ⚠️ Después del trial: $5/mes por servicio activo

### Cloudinary (Plan Free)
- ✅ 25 GB de almacenamiento
- ✅ 25 GB de ancho de banda/mes
- ✅ Suficiente para desarrollo y demos

---

## 🆘 **¿Necesitas ayuda?**

- Railway Docs: [https://docs.railway.app/](https://docs.railway.app/)
- Cloudinary Docs: [https://cloudinary.com/documentation](https://cloudinary.com/documentation)
- Spring Boot Docs: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)

---

**¡Buena suerte con el despliegue! 🚀**
