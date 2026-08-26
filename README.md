# BusinessFlow

BusinessFlow es un backend para un ERP que se desarrollará de forma progresiva. Este repositorio contiene por ahora la infraestructura base; todavía no implementa módulos funcionales del negocio.

## Stack

- Java 21
- Spring Boot 3.5.16
- Maven
- Spring Web, Spring Data JPA y Bean Validation
- MariaDB
- Docker y Docker Compose

## Requisitos

Para desarrollo local se necesita JDK 21, Maven 3.6.3 o posterior y una instancia de MariaDB compatible. Como alternativa, Docker con Docker Compose permite levantar la base de datos y la aplicación sin instalar Java ni Maven localmente.

## Configuración

La aplicación admite estas variables de entorno:

| Variable | Valor local predeterminado | Uso |
| --- | --- | --- |
| `DB_URL` | `jdbc:mariadb://localhost:3306/businessflow` | URL JDBC de MariaDB |
| `DB_USERNAME` | `businessflow` | Usuario de la aplicación |
| `DB_PASSWORD` | `businessflow_dev` | Contraseña de la aplicación |
| `SERVER_PORT` | `8080` | Puerto HTTP |

Los valores predeterminados son exclusivamente de desarrollo. Para personalizarlos con Docker, copia `.env.example` como `.env` y cambia sus valores. `.env` está excluido de Git. No se deben guardar credenciales reales en el repositorio.

## Ejecución local

Con una MariaDB inicializada y accesible mediante las variables anteriores:

```shell
mvn spring-boot:run
```

La configuración JPA usa `ddl-auto=validate`: Hibernate valida el esquema pero no lo crea ni lo modifica.

## Docker Compose

Para levantar MariaDB y el backend:

```shell
docker compose up --build
```

Para levantar solamente MariaDB y ejecutar después la aplicación con Maven:

```shell
docker compose up -d mariadb
mvn spring-boot:run
```

MariaDB publica el puerto `3306` y conserva los datos en el volumen `businessflow_mariadb_data`. El archivo `database/BusinessFlow.sql` se monta como script de inicialización y solo se ejecuta cuando el volumen de datos está vacío. El script contiene `DROP DATABASE IF EXISTS businessflow`, pero su ejecución queda aislada dentro del contenedor y volumen de este proyecto. No debe ejecutarse contra una base de datos externa sin revisarlo previamente.

Los cambios posteriores en el SQL no se aplican automáticamente a un volumen ya inicializado. La eliminación del volumen borra los datos y, por tanto, solo debe hacerse de manera deliberada.

## Tests y compilación

```shell
mvn test
mvn clean verify
```

Los tests desactivan la autoconfiguración de persistencia para validar el contexto y la capa HTTP sin depender de una base de datos externa.

## Health check

Con el backend arrancado:

```shell
curl http://localhost:8080/api/health
```

Respuesta esperada:

```json
{"status":"UP","service":"BusinessFlow"}
```

## Estructura actual

```text
src/main/java/dev/adriangabas/businessflow/
├── BusinessFlowApplication.java
└── health/HealthController.java
src/main/resources/application.yml
src/test/java/dev/adriangabas/businessflow/
database/BusinessFlow.sql
compose.yml
Dockerfile
```

El paquete base es `dev.adriangabas.businessflow`. Se añadirán paquetes de módulos cuando exista funcionalidad real que los justifique.
