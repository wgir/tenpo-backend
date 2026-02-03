## 1. Objetivo

Diseñar y desarrollar una API (backend) en Java con Spring Boot, siguiendo las mejores prácticas de desarrollo de software.  
El objetivo es evaluar habilidades técnicas para implementar una solución escalable, bien documentada y mantenible.

---

## 2. Contexto

La empresa **Tenpo** ofrece servicios de funcionarios a clientes (empresas).  
Para ello, cuenta con una plataforma que permite a sus clientes gestionar sus funcionarios y a estos últimos registrar sus transacciones.

---

## 3. Requerimientos Funcionales

### Funcionalidad Principal

Crear una aplicación que permita registrar **clientes**, **funcionarios** y **transacciones**.

### Clientes

- Crear nuevos clientes con los siguientes campos:
    - `id_cliente` (int)
    - `nombre_cliente` (varchar)
    - `rut_cliente` (varchar)
- Obtener todos los clientes
- Obtener un cliente por ID
- Actualizar un cliente
- Eliminar un cliente

### Funcionarios

- Crear nuevos funcionarios con los siguientes campos:
    - `id_funcionario` (int)
    - `nombre_funcionario` (varchar)
    - `rut_funcionario` (varchar)
    - `id_cliente` (int)
- Obtener todos los funcionarios
- Obtener un funcionario por ID
- Actualizar un funcionario
- Eliminar un funcionario

### Transacciones

- Crear nuevas transacciones con los siguientes campos:
    - `id_transaccion` (int)
    - `monto_transaccion` en pesos (int)
    - `giro_comercio` de la transacción (varchar)
    - `id_funcionario` (int)
    - `fecha_transaccion` (datetime)
- Obtener todas las transacciones
- Obtener una transacción por ID
- Actualizar una transacción
- Eliminar una transacción

### Restricciones

- Cada cliente puede tener un **máximo de 100 transacciones**.
- Las transacciones **no pueden tener montos negativos**.
- La fecha de la transacción **no puede ser superior a la fecha y hora actual**.

---

## 4. Requerimientos Técnicos

### Backend

- **Spring Boot**
    - Implementar una API REST.
    - Endpoint sugerido: `/transaction` (`GET`, `POST`, `PUT`, `DELETE`)

- **Base de Datos**
    - PostgreSQL como base de datos relacional. Version 18.1

- **Rate Limiting**
    - Máximo **3 requests por minuto por cliente**.

- **Pruebas**
    - Pruebas unitarias para servicios, repositorios y controladores.
    - Uso de mocks es un plus.

- **Manejo de Errores**
    - Manejador global de errores HTTP.
    - Ejemplo: error 500 para fallas de servidor.

- **Documentación**
    - Swagger + Swagger UI.

---

## 5. Criterios de Evaluación

- Correctitud
- Calidad del código
- Pruebas
- Documentación
- Uso de Docker
- Escalabilidad y eficiencia

---

## 6. Entregables

- Repositorio público (GitHub o similar)
- README.md con instrucciones
- Imagen Docker o docker-compose
