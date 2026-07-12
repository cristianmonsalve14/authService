# authService

Autenticación, JWT y administración de usuarios de **Libro Digital**.

## Puerto

http://localhost:8091 — vía gateway: http://localhost:8090

## Configuración (secretos)

1. Copiar `src/main/resources/application-local.properties.example` → `application-local.properties`
2. Definir `spring.datasource.password` y `jwt.secret` (misma clave que academic, attendance y gateway)

```sh
mvn spring-boot:run
```

## Endpoints

| Método | Ruta | Acceso |
|--------|------|--------|
| POST | `/auth/login` | Público |
| POST | `/auth/refresh` | Público |
| GET | `/auth/me` | JWT |
| POST | `/auth/register` | Deshabilitado (mensaje de error) |
| CRUD | `/admin/users` | Solo `ADMINISTRADOR` |

## Usuarios demo

Password: `test1234`

- `admin_colegio` — ADMINISTRADOR  
- `prof_castillo` — DOCENTE  
- `estudiante_demo` — ESTUDIANTE  
- `apoderado_demo` — APODERADO  

## Stack

Java 21, Spring Boot 4.1, Security + JWT, JPA, PostgreSQL (`librodigital_auth`).
