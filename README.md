# Gym Control - Examen práctico

Sistema web para gimnasio hecho con Spring Boot 3, Java 21, Spring Security, JPA y Thymeleaf.

## Incluye
- Login y logout
- Roles ADMIN y CLIENTE
- Registrar, editar, buscar y activar/desactivar clientes
- Crear tipos de membresía
- Asignar membresías con inicio y vencimiento
- Control de asistencia con bloqueo por membresía vencida
- Registro de pagos
- Dashboard
- H2 para ejecutar de inmediato
- Configuración preparada para PostgreSQL AWS RDS

## Ejecutar
1. Abrir esta carpeta en VS Code.
2. Tener Java 21 instalado.
3. En terminal: `mvn spring-boot:run`
4. Abrir `http://localhost:8080`

## Usuarios de prueba
- Administrador: `admin` / `Admin123*`
- Cliente: `cliente` / `Cliente123*`

## AWS RDS
El archivo `src/main/resources/application-aws.properties` está preparado para PostgreSQL.
Más adelante reemplaza endpoint, usuario y contraseña y ejecuta con perfil `aws`.
