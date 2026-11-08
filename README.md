# Backend GamerSpawn

Backend de GamerSpawn desarrollado con Spring Boot. La aplicación usa MySQL,
JWT para autenticación y se ejecuta en el puerto `8081`.

## Requisitos

- Java 21
- Docker
- MySQL ejecutándose en Docker

## 1. Iniciar MySQL

Crear el contenedor:

```cmd
docker run --name mysql-gamerspawn -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 -d mysql:latest
```

Si el contenedor ya existe, iniciarlo con:

```cmd
docker start mysql-gamerspawn
```

Crear la base de datos:

```cmd
docker exec -it mysql-gamerspawn mysql -uroot -ppassword -e "CREATE DATABASE IF NOT EXISTS gamerspawn;"
```

La contraseña usada en el contenedor debe coincidir con
`spring.datasource.password` en `src/main/resources/application.properties`.

Para crear el esquema y cargar los eventos iniciales mediante un único script,
ejecutar desde la carpeta `backend`:

```cmd
docker exec -i mysql-gamerspawn mysql -uroot -ppassword < database\gamerspawn.sql
```

El script recrea las tablas `event` y `event_photos`. Los usuarios y sus
contraseñas son generados por `DataSeeder` al iniciar la aplicación.

## 2. Generar y configurar la clave JWT

La aplicación necesita una variable de entorno llamada `JWT_SECRET`. En
PowerShell, genera una clave aleatoria de 64 bytes y configúrala para el
usuario actual:

```powershell
$bytes = New-Object byte[] 64
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Environment]::SetEnvironmentVariable("JWT_SECRET", [Convert]::ToBase64String($bytes), "User")
```


## 3. Ejecutar el backend

Desde esta carpeta:

```cmd
mvnw.cmd spring-boot:run
```

La aplicación quedará disponible en:

```text
http://localhost:8081
```

Hibernate crea o actualiza las tablas automáticamente. Si no se utiliza el
script SQL, al iniciar `DataSeeder` crea estos usuarios si todavía no existen:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin` | `ROLE_ADMIN` |
| `user1` | `user1` | `ROLE_USER` |
| `user2` | `user2` | `ROLE_USER` |

## 4. Probar con Postman

### Login

- Método: `POST`
- URL: `http://localhost:8081/login`
- Body: `x-www-form-urlencoded`

| Key | Value |
|---|---|
| `user` | `admin` |
| `encryptedPass` | `admin` |

La respuesta es un token JWT. Cópiar para las siguientes solicitudes.

### Endpoint público

```text
GET http://localhost:8081/api/public/home
```

No requiere token.

### Endpoint privado

```text
GET http://localhost:8081/api/private/home
```

En Postman seleccionar **Authorization > Bearer Token** y pegar el JWT recibido.

Las rutas bajo `/api/admin/**` requieren el rol `ROLE_ADMIN`.
