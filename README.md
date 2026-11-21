# Backend GamerSpawn

API REST de GamerSpawn desarrollada con Spring Boot. Usa MySQL para almacenar
los datos y JWT para la autenticación, y se ejecuta en el puerto `8081`. El
frontend web está en
[gamer-spawn](https://github.com/SebaleSan/gamer-spawn).

## Ejecutar todo con Docker Compose (recomendado)

Levanta MySQL, este backend y el frontend con un solo comando. El único
requisito es [Docker Desktop](https://www.docker.com/products/docker-desktop/).
No es necesario instalar Java ni MySQL.

1. Clonar ambos repositorios en la misma carpeta y abrir una terminal en la
   carpeta del backend:

   ```powershell
   git clone https://github.com/SebaleSan/gamer-spawn-backend.git
   git clone https://github.com/SebaleSan/gamer-spawn.git
   cd gamer-spawn-backend
   ```

   La imagen del frontend se construye desde la carpeta `../gamer-spawn`. Si se
   clonó en otra ubicación, agregar `FRONTEND_DIR=<ruta>` al archivo `.env`.

2. Crear el archivo `.env` con una contraseña de base de datos y una clave JWT
   aleatorias.

   PowerShell:

   ```powershell
   $bytes = New-Object byte[] 64
   [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
   "DB_PASSWORD=$([guid]::NewGuid().ToString('N'))`nJWT_SECRET=$([Convert]::ToBase64String($bytes))" | Set-Content .env -Encoding ascii
   ```

   Linux o macOS:

   ```bash
   printf "DB_PASSWORD=%s\nJWT_SECRET=%s\n" "$(openssl rand -hex 16)" "$(openssl rand -base64 64 | tr -d '\n')" > .env
   ```

   `.env.example` muestra las variables requeridas. Git ignora el archivo `.env`.

3. Construir e iniciar los contenedores:

   ```powershell
   docker compose up -d --build
   ```

   La primera construcción tarda algunos minutos. Hibernate crea las tablas y
   los usuarios de prueba, los eventos de ejemplo y sus fotos se cargan
   automáticamente.

4. Abrir http://localhost:8080 e iniciar sesión con uno de los usuarios de
   prueba.

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin` | `ROLE_ADMIN` |
| `user1` | `user1` | `ROLE_USER` |
| `user2` | `user2` | `ROLE_USER` |

También se pueden crear cuentas nuevas desde la página de registro.

Para detener los contenedores, ejecutar `docker compose down`. Con `-v` además
se elimina el volumen de la base de datos.

Los puertos `8080` y `8081` deben estar libres antes de iniciar.

## Ejecutar localmente sin Docker Compose

Requisitos: Java 21 y Docker (para MySQL).

1. Iniciar MySQL:

   ```powershell
   docker run --name mysql-gamerspawn -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=gamerspawn -p 3306:3306 -d mysql:8.4
   ```

   Si el contenedor ya existe, iniciarlo con `docker start mysql-gamerspawn`.
   La contraseña debe coincidir con `spring.datasource.password` en
   `src/main/resources/application.properties`.

2. Crear la variable de entorno `JWT_SECRET` para el usuario actual:

   ```powershell
   $bytes = New-Object byte[] 64
   [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
   [Environment]::SetEnvironmentVariable("JWT_SECRET", [Convert]::ToBase64String($bytes), "User")
   ```

   Después, abrir una terminal nueva para que la variable se cargue.

3. Ejecutar el backend:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

Al iniciar, Hibernate crea o actualiza las tablas, y `DataSeeder` crea los
usuarios de prueba, los eventos de ejemplo y sus fotos si todavía no existen.

`database/gamerspawn.sql` es un script opcional que reconstruye el esquema con
los eventos de ejemplo. Elimina todas las tablas, incluidos los usuarios, los
comentarios y las valoraciones:

```powershell
Get-Content database\gamerspawn.sql | docker exec -i mysql-gamerspawn mysql -uroot -ppassword
```

## API

### Públicas

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/login` | Devuelve un JWT. Parámetros de formulario `user` y `encryptedPass` |
| `POST` | `/register` | Crea un usuario. Cuerpo JSON `username`, `email`, `password` |
| `GET` | `/api/events` | Lista los eventos |
| `GET` | `/api/events/search` | Filtra eventos por `title`, `videoGame`, `eventType`, `category`, `country`, `city`, `modality` |

### Privadas

Requieren el encabezado `Authorization: Bearer <token>`.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/private/events` | Lista los eventos |
| `GET` | `/api/private/events/{id}` | Detalle de un evento |
| `GET` | `/api/private/events/mine` | Eventos creados por el usuario actual |
| `POST` | `/api/private/events` | Crea un evento |
| `GET`, `POST` | `/api/private/events/{eventId}/comments` | Lista o agrega comentarios. Cuerpo JSON `content` |
| `PUT`, `DELETE` | `/api/private/events/{eventId}/comments/{commentId}` | Edita o elimina un comentario (autor o administrador) |
| `GET`, `PUT`, `DELETE` | `/api/private/events/{eventId}/rating` | Obtiene, asigna o elimina la valoración del usuario. Cuerpo JSON `score` (1 a 5) |
| `GET`, `POST` | `/api/private/events/{eventId}/media` | Lista o agrega la URL de una foto o un video. Cuerpo JSON `type` (`PHOTO` o `VIDEO`) y `url` (https) |
| `DELETE` | `/api/private/events/{eventId}/media/{mediaId}` | Elimina una foto o un video (autor o administrador) |

Las rutas bajo `/api/admin/**` requieren el rol `ROLE_ADMIN`.

### Ejemplo con Postman

1. `POST http://localhost:8081/login` con un cuerpo `x-www-form-urlencoded`:
   `user=admin` y `encryptedPass=admin`. Copiar el token de la respuesta.
2. `GET http://localhost:8081/api/private/events` con
   **Authorization > Bearer Token** configurado con ese token.
