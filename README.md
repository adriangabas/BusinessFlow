# BusinessFlow

BusinessFlow es un backend para un ERP que se desarrollará de forma progresiva. Actualmente incluye la infraestructura base y los módulos de usuarios, roles, clientes, categorías de producto y productos.

## Stack

- Java 21
- Spring Boot 4.1.1
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

## Categorías de producto

El CRUD REST está disponible en `/api/categorias-producto`:

| Método | Ruta | Resultado |
| --- | --- | --- |
| `POST` | `/api/categorias-producto` | Crea una categoría (`201`) |
| `GET` | `/api/categorias-producto` | Lista las categorías no eliminadas (`200`) |
| `GET` | `/api/categorias-producto/{id}` | Obtiene una categoría (`200`) |
| `PUT` | `/api/categorias-producto/{id}` | Actualiza una categoría (`200`) |
| `DELETE` | `/api/categorias-producto/{id}` | Desactiva una categoría (`204`) |

Ejemplo de creación:

```shell
curl -X POST http://localhost:8080/api/categorias-producto \
  -H "Content-Type: application/json" \
  -d '{"codigo":"ALIM","nombre":"Alimentación","descripcion":"Productos alimentarios"}'
```

La eliminación es lógica: asigna el estado `INACTIVO`, registra `deleted_at` y oculta la categoría de las consultas. El código continúa reservado por la restricción única del esquema.

## Clientes

El CRUD REST está disponible en `/api/clientes`:

| Método | Ruta | Resultado |
| --- | --- | --- |
| `POST` | `/api/clientes` | Crea un cliente (`201`) |
| `GET` | `/api/clientes` | Lista los clientes no eliminados (`200`) |
| `GET` | `/api/clientes/{id}` | Obtiene un cliente (`200`) |
| `PUT` | `/api/clientes/{id}` | Actualiza un cliente (`200`) |
| `DELETE` | `/api/clientes/{id}` | Elimina lógicamente un cliente (`204`) |

Los campos obligatorios son `codigo`, `tipoCliente` (`EMPRESA` o `PARTICULAR`) y `nombre`. El código se
normaliza eliminando espacios exteriores y convirtiéndolo a mayúsculas. `nombreComercial`,
`identificacionFiscal`, `email`, `telefono`, `direccion`, `codigoPostal`, `localidad`, `provincia`, `pais` y
`observaciones` son opcionales. La identificación fiscal y el email no son únicos.

Ejemplo de creación:

```shell
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"codigo":"CLI-1","tipoCliente":"EMPRESA","nombre":"Cliente Uno","email":"cliente@example.com"}'
```

Un cliente `INACTIVO` continúa visible y accesible mientras `deleted_at` sea nulo. `DELETE` realiza un borrado
lógico: asigna `INACTIVO`, registra `deleted_at`, oculta el cliente de las consultas normales y mantiene
reservado su código.

## Productos

El CRUD REST está disponible en `/api/productos`:

| Método | Ruta | Resultado |
| --- | --- | --- |
| `POST` | `/api/productos` | Crea un producto (`201`) |
| `GET` | `/api/productos` | Lista los productos no eliminados (`200`) |
| `GET` | `/api/productos/{id}` | Obtiene un producto (`200`) |
| `PUT` | `/api/productos/{id}` | Actualiza un producto (`200`) |
| `DELETE` | `/api/productos/{id}` | Elimina lógicamente un producto (`204`) |

Los campos principales de escritura son `codigo`, `nombre`, `categoriaId`, `precioVenta`, `precioCoste`,
`stockMinimo` y `unidadMedida`. Los valores permitidos para `unidadMedida` son `UNIDAD`, `KG`, `LITRO`,
`METRO`, `CAJA` y `PAQUETE`. La respuesta incluye una representación resumida de la categoría con su id,
código y nombre.

Ejemplo de creación:

```shell
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"codigo":"PROD-1","nombre":"Producto","categoriaId":1,"precioVenta":12.34,"precioCoste":5.67,"stockMinimo":3,"unidadMedida":"UNIDAD"}'
```

La categoría debe existir y no estar eliminada lógicamente. Una categoría `INACTIVA` sin `deleted_at` sigue
siendo válida para conservar la compatibilidad con el modelo. El borrado del producto también es lógico:
asigna `INACTIVO`, registra `deleted_at`, lo oculta de las consultas normales y mantiene reservado su código.

## Usuarios y roles

El CRUD de usuarios está disponible en `/api/usuarios` y el de roles en `/api/roles`. Ambos ofrecen `POST`,
`GET` de listado, `GET /{id}`, `PUT /{id}` y `DELETE /{id}` con respuestas `201`, `200` o `204` según la
operación. Los conflictos por email o código duplicado devuelven `409` y los recursos inexistentes o eliminados,
`404`.

Los estados de usuario son `PENDING`, `ACTIVE`, `BLOCKED` e `INACTIVE`; en creación se usa `PENDING` por
defecto. Los estados de rol son `ACTIVE` e `INACTIVE`; en creación se usa `ACTIVE` por defecto. `isSystem` es
`true` por defecto al crear un rol y debe indicarse explícitamente al actualizarlo.

La creación de un usuario requiere `nombre`, `email` y `passwordHash`. Este último se recibe exclusivamente
como un valor opaco ya generado fuera de BusinessFlow, no se devuelve en ninguna respuesta y puede omitirse en
una actualización para conservar el valor existente. No deben enviarse contraseñas en claro. Este módulo aún no
implementa login, hashing, autenticación, autorización ni Spring Security.

Los roles de un usuario se administran con estas rutas:

| Método | Ruta | Resultado |
| --- | --- | --- |
| `GET` | `/api/usuarios/{usuarioId}/roles` | Lista los roles asignados (`200`) |
| `POST` | `/api/usuarios/{usuarioId}/roles/{rolId}` | Asigna un rol (`201`) |
| `DELETE` | `/api/usuarios/{usuarioId}/roles/{rolId}` | Quita la asignación (`204`) |

La tabla puente se modela mediante una entidad de asociación porque conserva la fecha `created_at`. Las
asignaciones duplicadas devuelven `409`; asignar exige que tanto el usuario como el rol existan y no estén
eliminados.

Usuarios y roles usan borrado lógico: `DELETE` cambia su estado a `INACTIVE` y registra `deleted_at`. Desde ese
momento quedan fuera de consultas normales, pero su email o código continúa reservado por la restricción única.
Un registro `INACTIVE` con `deleted_at` nulo sigue existiendo y continúa accesible.

## Estructura actual

```text
src/main/java/dev/adriangabas/businessflow/
├── BusinessFlowApplication.java
├── categoria/
├── cliente/
├── error/
├── producto/
├── rol/
├── usuario/
└── health/HealthController.java
src/main/resources/application.yml
src/test/java/dev/adriangabas/businessflow/
database/BusinessFlow.sql
compose.yml
Dockerfile
```

El paquete base es `dev.adriangabas.businessflow`; cada módulo funcional mantiene separadas sus capas HTTP, de negocio y persistencia.
