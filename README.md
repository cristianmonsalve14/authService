# 🔐 authService

Microservicio de autenticación y gestión de usuarios de la plataforma **Libro Digital**.

Encargado del registro, autenticación, emisión y validación de tokens JWT, así como de la gestión de roles y permisos de usuarios. Es el núcleo de seguridad dentro de la arquitectura de microservicios.

---

## 🧠 Arquitectura

Este microservicio está construido siguiendo una arquitectura por capas:

- **Controller** → Manejo de solicitudes HTTP
- **Service** → Lógica de negocio
- **Repository** → Acceso a base de datos
- **DTO** → Transferencia de datos
- **Security** → Configuración de seguridad y JWT
- **Exception** → Manejo global de errores

---


## ⚙️ Stack tecnológico

- Java 21
- Spring Boot
- Spring Security
- JWT (io.jsonwebtoken:jjwt)
- BCrypt
- Spring Web
- Spring Data JPA
- Maven
- PostgreSQL

---


## 🚀 Instalación y ejecución
1. Clona este repositorio.

```bash
git clone https://github.com/cristianmonsalve14/authService.git
cd authService
``

2. Configura la conexión a la base de datos en `src/main/resources/application.properties`.
3. Compila y ejecuta con:
   ```sh
   mvn clean spring-boot:run
   ```

## Endpoints principales
- `POST /auth/register` — Registro de usuario (username/email y password)
- `POST /auth/login` — Autenticación y obtención de JWT

## Seguridad
- Contraseñas almacenadas con BCrypt.
- Autenticación basada en JWT.
- Roles y permisos gestionados en base de datos.

## Pruebas unitarias
- Pruebas para AuthServiceImpl: login, registro, errores de autenticación y usuario existente.
- Pruebas para JwtUtil: generación y validación de tokens, manejo de tokens inválidos.
- Ejecuta las pruebas con:
   ```sh
   mvn test
   ```

## Autor
- Cristian Monsalve
---
Este microservicio es parte del ecosistema Libro Digital. Más información y documentación general en el repositorio de infraestructura.