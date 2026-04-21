# authService

Microservicio de autenticación y gestión de usuarios de la plataforma Libro Digital.

Encargado del registro, autenticación, emisión y validación de tokens JWT, así como de la gestión de roles y permisos de usuarios. Desarrollado con Spring Boot 4.0.5, Java 21 y Maven. Es el núcleo de seguridad dentro de la arquitectura de microservicios.

## Stack tecnológico
- Java 21
- Spring Boot 4.0.5
- Spring Security
- JWT
- Spring Web
- Spring Data JPA
- Maven
- PostgreSQL

## Instalación y ejecución
1. Clona este repositorio.
2. Configura la conexión a la base de datos en `src/main/resources/application.properties`.
3. Compila y ejecuta con:
   ```sh
   mvn clean spring-boot:run
   ```

## Autores
- Cristian Monsalve
- Hector Olivares

---
Este microservicio es parte del ecosistema Libro Digital. Más información y documentación general en el repositorio de infraestructura.