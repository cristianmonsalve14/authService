# 🔐 authService

Microservicio de autenticación y gestión de usuarios de la plataforma **Libro Digital**.

Encargado del registro, autenticación y generación de tokens JWT, así como del control de acceso a los microservicios del sistema.

---

## 🧠 Arquitectura

Este microservicio sigue una arquitectura por capas:

- Controller → Manejo de endpoints HTTP  
- Service → Lógica de negocio  
- Repository → Acceso a base de datos  
- DTO → Transferencia de datos  
- Security → Configuración de JWT y seguridad  
- Exception → Manejo de errores  

---

## ⚙️ Stack Tecnológico

- Java 21  
- Spring Boot  
- Spring Security  
- JWT (io.jsonwebtoken)  
- BCrypt (cifrado de contraseñas)  
- Spring Web  
- Spring Data JPA  
- Maven  
- PostgreSQL  

---

## 🚀 Instalación y Ejecución

### 1. Configuración

Editar archivo:

src/main/resources/application.properties

Ejemplo:

spring.datasource.url=jdbc:postgresql://localhost:5432/librodigital_auth  
spring.datasource.username=postgres  
spring.datasource.password=tu_password  

server.port=8091  

jwt.secret=tuClaveSecretaParaJWT  

---

### 2. Ejecutar aplicación

mvn clean spring-boot:run

El servicio estará disponible en:

http://localhost:8091  

---

## 🔑 Endpoints principales

- POST /auth/register → Registro de usuario  
- POST /auth/login → Autenticación y obtención de token JWT  

---

## 🔐 Seguridad

El sistema utiliza autenticación basada en JWT:

✔ Contraseñas encriptadas con BCrypt  
✔ Generación de token en login  
✔ Validación en cada request  

Flujo:

1. Usuario envía credenciales en /auth/login  
2. Se valida usuario y contraseña  
3. Se genera token JWT  
4. El frontend guarda el token  
5. El token se usa en cada petición:

Authorization: Bearer {token}

---

## 🔗 Integración con otros servicios

Este microservicio trabaja junto a:

- academicService → consume el JWT para validar acceso  
- frontend → envía token en cada petición  

---

## 🏗️ Estructura del Proyecto

authService/
├── controller/
├── service/
├── service/impl/
├── repository/
├── dto/
├── security/
├── exception/

---

## ✅ Estado del Proyecto

✔ Autenticación funcional  
✔ Generación de JWT  
✔ Validación de credenciales  
✔ Integración con frontend  
✔ Integración con microservicios  

---

## 👨‍💻 Autor

Cristian Monsalve  

---

## 📌 Observaciones

Este microservicio es el encargado de la seguridad del sistema, permitiendo la autenticación centralizada mediante JWT.

Se aplican buenas prácticas de seguridad como cifrado de contraseñas, validación de tokens y separación por capas.
``