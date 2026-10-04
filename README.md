# API de consulta de precios

## Objetivo

Desarrollar una API REST que permita obtener el **precio final de un producto para una cadena en una fecha y hora determinadas**, aplicando la tarifa correspondiente de acuerdo a su periodo de vigencia y prioridad.

## Descripci&oacute;n

La aplicaci&oacute;n consulta la informaci&oacute;n de precios almacenada en la base de datos y determina qu&eacute; tarifa debe aplicarse en funci&oacute;n de:

* Identificador de la cadena (`brandId`).
* Identificador del producto (`productId`).
* Fecha y hora de aplicaci&oacute;n.

Cuando existen varias tarifas aplicables para un mismo producto y periodo de tiempo, se selecciona aquella que tenga la **mayor prioridad**.

La aplicaci&oacute;n utiliza una base de datos **H2 en memoria**, inicializada con los datos de ejemplo proporcionados.

## Tecnolog&iacute;as

* Java 21
* Spring Boot 4
* H2 Database
* JUnit 5

## Base de Datos

La tabla `PRICES` contiene los siguientes campos:

- `ID` (Campo agregado): Clave Primaria de la tabla. Se agrega ya que a&uacute;n con el campo `PRIORITY` puede haber duplicidad de registros.
- `BRAND_ID` : Identificador de la cadena.
- `START_DATE` : Fecha y hora de inicio de aplicaci&oacute;n de la tarifa.
- `END_DATE` : Fecha y hora de finalizaci&oacute;n de la tarifa. 
- `TARIFF_ID`: Identificador de la tarifa (Antes `PRICE_LIST`, se cambia para que tenga mejor sentido sem&aacute;ntico).
- `PRODUCT_ID`: Identificador del producto.
- `PRIORITY` : Prioridad de aplicaci&oacute;n de la tarifa. En caso de solapamiento, se selecciona la de mayor prioridad.
- `PRICE` : Precio final de venta. 
- `CURRENCY_CODE` : Moneda del precio (Antes `CURR`, se cambia para que tenga mejor sentido sem&aacute;ntico).
- `CREATED_AT` (Campo agregado): Fecha de creaci&oacute;n. Se agrega para que en caso que no haya desempate, se seleccione el registro m&aacute;s reciente.

Se crea un &iacute;ndice compuesto (CREATE INDEX idx_prices_brand_product ON prices (brand_id, product_id)) sobre BRAND_ID y PRODUCT_ID debido a que ambos atributos constituyen el criterio principal de b&uacute;squeda del precio final para un producto dentro de una cadena. El &iacute;ndice permite reducir significativamente el conjunto de registros candidatos antes de aplicar las condiciones de vigencia (START_DATE/END_DATE) y los criterios de desempate (PRIORITY y CREATED_AT).
Esta decisi&oacute;n parte del supuesto de que la frecuencia de escrituras sobre la tabla es lo suficientemente moderada como para que el mantenimiento de este &iacute;ndice no tenga un impacto significativo en el rendimiento de las operaciones de inserci&oacute;n y actualizaci&oacute;n. La conveniencia de un &iacute;ndice adicional o de ampliar el existente deber&aacute; validarse mediante el plan de ejecuci&oacute;n y m&eacute;tricas reales de la carga.

## Criterio de selecci&oacute;n del precio

Para una fecha, producto y cadena determinados, se consideran las tarifas cuyo periodo de aplicaci&oacute;n contiene la fecha consultada.

Si existen varias tarifas aplicables, se selecciona la que tenga **mayor valor de `PRIORITY`**.

Si existen varias tarifas aplicables con la misma prioridad se selecciona la que tenga **mayor valor de `CREATED_AT`**.

Si existen varias tarifas aplicables con la misma prioridad y misma fecha de creaci&oacute;n se selecciona por **menor valor de `ID`**.

Por ejemplo, para el **14/06 a las 16:00**, son aplicables las tarifas 1 y 2. Al tener la tarifa 2 una prioridad superior, el precio resultante es:

```text
25.45 EUR
```
## Estructura de la aplicaci&oacute;n

Servicio REST de pricing construido con Java 21, Maven, Spring Boot y utilizando metodolog&iacute;a API First.

El proyecto utiliza dos proyectos maven independientes:

* `pricing-api-contract`: contiene el contrato de la API y los recursos OpenAPI.
* `pricing-service`: servicio REST que utiliza `pricing-api-contract`.

La ejecuci&oacute;n mediante Docker permite compilar ambos proyectos y utilizar el contrato generado por `pricing-api-contract` durante la compilaci&oacute;n de `pricing-service`.

## Estructura del proyecto

```text
.
├── pricing-api-contract/
│   ├── pom.xml
│   ├── pricing-api/
│   └── target/
│
├── pricing-service/
│   ├── pom.xml
│   └── src/
│
├── docker/
│   ├── Dockerfile
│   ├── Dockerfile.jacoco
|   ├── Dockerfile.pit
│   ├── docker-compose.yml
|   ├── pit-report/
│   └── jacoco-report/
│
├── docs/
├── .gitignore
└── README.md
```
> `docker/jacoco-report/` contiene los informes generados por JaCoCo y est&aacute; excluido de Git.
> `docker/pit-report/` contiene los informes generados por pit y est&aacute; excluido de Git.

## Endpoint

### Consultar precio

```http
GET /{version}/ecommerce/price
```
Actualmente: GET /v2/ecommerce/price

Par&aacute;metros:
- `brandId` : Identificador de la cadena. 
- `productId` : Identificador del producto. 
- `applicationDate` : Fecha y hora para la que se desea obtener el precio.

| Par&aacute;metro         | Tipo       | Descripci&oacute;n                                          |
| ----------------- | ---------- | ---------------------------------------------------- |
| `applicationDate` | `datetime` | Fecha y hora para la que se desea obtener el precio. |
| `productId`       | `long`     | Identificador del producto.                          |
| `brandId`         | `long`     | Identificador de la cadena.                          |

Ejemplo:

```http
GET /v2/ecommerce/price?brandId=1&productId=35455&applicationDate=2026-06-14T16:00:00
```

Respuesta:

```json
{
    "productId": 35455,
    "brandId": 1,
    "tariffId": 2,
    "startDate": "2026-06-14T15:00:00",
    "endDate": "2026-06-14T18:30:00",
    "price": 25.45,
    "currencyCode": "EUR"
}
```

### Documentaci&oacute;n de la API

La documentaci&oacute;n de la API se encuentra disponible en esta url:

```http
/swagger-ui/index.html
```

Ejemplo:
```http
http://localhost:8080/swagger-ui/index.html
```

### Consultar precio

## Datos iniciales

La aplicaci&oacute;n se inicializa con las siguientes tarifas:

| Brand | Producto | Tarifa | Inicio           | Fin              | Prioridad | Precio | Moneda |
| ----: | -------: | -----: | ---------------- | ---------------- | --------: | -----: | :----: |
|     1 |    35455 |      1 | 14/06/2026 00:00 | 31/12/2026 23:59 |         0 |  35.50 |   EUR  |
|     1 |    35455 |      2 | 14/06/2026 15:00 | 14/06/2026 18:30 |         1 |  25.45 |   EUR  |
|     1 |    35455 |      3 | 15/06/2026 00:00 | 15/06/2026 11:00 |         1 |  30.50 |   EUR  |
|     1 |    35455 |      4 | 15/06/2026 16:00 | 31/12/2026 23:59 |         1 |  38.95 |   EUR  |
|     2 |    35455 |     10 | 14/06/2026 00:00 | 31/12/2026 23:59 |         0 |  42.50 |   CNY  |
|     2 |    45566 |     11 | 15/06/2026 00:00 | 31/12/2026 23:59 |         0 |  59.99 |   CNY  |
|     2 |    45566 |     12 | 01/07/2025 00:00 | 31/12/2025 23:59 |         1 |  54.99 |   GBP  |
|     2 |    45566 |     13 | 15/07/2026 00:00 | 15/08/2026 23:59 |         2 |  49.99 |   GBP  |
|     3 |    78901 |     20 | 14/06/2025 00:00 | 31/12/2025 23:59 |         0 |  79.95 |   EUR  |
|     1 |    78901 |     21 | 14/06/2025 00:00 | 31/12/2025 23:59 |         0 |  69.95 |   EUR  |
|     1 |    35455 |     22 | 16/06/2026 00:00 | 30/06/2026 23:59 |         0 |  34.95 |   EUR  |
|     1 |    35455 |     23 | 16/06/2026 10:00 | 16/06/2026 14:00 |         1 |  29.95 |   EUR  |
|     1 |    35455 |     24 | 17/06/2026 00:00 | 20/06/2026 23:59 |         1 |  31.50 |   EUR  |
|     1 |    35455 |     25 | 18/06/2026 18:00 | 25/06/2026 23:59 |         2 |  27.99 |   EUR  |
|     1 |    35456 |     26 | 21/06/2026 00:00 | 15/07/2026 23:59 |         0 |  45.75 |   EUR  |
|     1 |    35456 |     27 | 22/06/2026 09:00 | 22/06/2026 17:00 |         1 |  39.90 |   EUR  |
|     1 |    35456 |     28 | 23/06/2026 00:00 | 31/08/2026 23:59 |         0 |  47.25 |   EUR  |
|     1 |    35457 |     29 | 01/07/2026 00:00 | 30/09/2026 23:59 |         0 |  52.40 |   EUR  |
|     1 |    35457 |     30 | 10/07/2026 12:00 | 20/07/2026 20:00 |         1 |  48.50 |   EUR  |
|     2 |    45566 |     31 | 14/06/2026 00:00 | 31/12/2026 23:59 |         0 |  61.99 |   CNY  |
|     2 |    45566 |     32 | 20/06/2026 00:00 | 31/07/2026 23:59 |         1 |  57.49 |   CNY  |
|     2 |    45566 |     33 | 05/07/2026 08:00 | 10/07/2026 18:00 |         2 |  51.99 |   GBP  |
|     2 |    45566 |     34 | 20/07/2026 00:00 | 20/08/2026 23:59 |         1 |  53.75 |   GBP  |
|     2 |    45567 |     35 | 01/08/2026 00:00 | 31/12/2026 23:59 |         0 |  64.50 |   CNY  |
|     2 |    45567 |     36 | 10/08/2026 10:00 | 25/08/2026 22:00 |         1 |  59.99 |   CNY  |
|     2 |    45567 |     37 | 01/09/2026 00:00 | 31/10/2026 23:59 |         0 |  68.95 |   GBP  |
|     2 |    45567 |     38 | 15/09/2026 00:00 | 30/09/2026 23:59 |         2 |  55.95 |   GBP  |
|     3 |    78901 |     39 | 14/06/2026 00:00 | 31/12/2026 23:59 |         0 |  82.95 |   EUR  |
|     3 |    78901 |     40 | 18/06/2026 14:00 | 25/06/2026 19:00 |         1 |  74.95 |   EUR  |
|     3 |    78901 |     41 | 01/07/2026 00:00 | 31/08/2026 23:59 |         1 |  76.50 |   EUR  |
|     3 |    78901 |     42 | 15/07/2026 00:00 | 31/07/2026 23:59 |         2 |  69.95 |   EUR  |
|     3 |    78902 |     43 | 01/09/2026 00:00 | 31/12/2026 23:59 |         0 |  91.25 |   EUR  |
|     3 |    78902 |     44 | 10/09/2026 09:00 | 20/09/2026 18:00 |         1 |  84.99 |   EUR  |
|     3 |    78902 |     45 | 01/10/2026 00:00 | 30/11/2026 23:59 |         0 |  95.50 |   EUR  |
|     1 |    90001 |     46 | 01/08/2026 00:00 | 31/10/2026 23:59 |         0 |  44.95 |   EUR  |
|     1 |    90001 |     47 | 15/08/2026 16:00 | 20/08/2026 20:00 |         1 |  39.95 |   EUR  |
|     1 |    90002 |     48 | 01/09/2026 00:00 | 31/12/2026 23:59 |         0 |  49.90 |   EUR  |
|     2 |    90003 |     49 | 01/10/2026 00:00 | 31/12/2026 23:59 |         0 |  72.50 |   CNY  |
|     2 |    90003 |     50 | 15/10/2026 12:00 | 15/11/2026 18:00 |         1 |  65.99 |   GBP  |
|     3 |    90004 |     51 | 01/11/2026 00:00 | 31/12/2026 23:59 |         0 | 105.95 |   EUR  |
|     3 |    90004 |     52 | 15/11/2026 10:00 | 30/11/2026 22:00 |         1 |  97.50 |   EUR  |

## Tests

Se han implementado pruebas de integraci&oacute;n sobre el endpoint REST para validar los siguientes escenarios:

| Test | Fecha y hora | Producto | Brand | Tarifa esperada | Precio esperado |
| ---- | ------------ | -------: | ----: | --------------: | --------------: |
| 1    | 14/06 10:00  |    35455 |     1 |               1 |       35.50 EUR |
| 2    | 14/06 16:00  |    35455 |     1 |               2 |       25.45 EUR |
| 3    | 14/06 21:00  |    35455 |     1 |               1 |       35.50 EUR |
| 4    | 15/06 10:00  |    35455 |     1 |               3 |       30.50 EUR |
| 5    | 16/06 21:00  |    35455 |     1 |               4 |       38.95 EUR |

## Requisitos para ejecutar la aplicaci&oacute;n con Docker

Para ejecutar el proyecto mediante Docker es necesario tener instalado:

* Docker
* Docker Compose

## Ejecutar la aplicaci&oacute;n

Desde la ra&iacute;z del proyecto:

```bash
docker compose -f docker/docker-compose.yml up --build pricing-service
```

La aplicaci&oacute;n quedar&aacute; disponible en:

```text
http://localhost:8080
```

## Ejecutar los tests

Los tests se ejecutan mediante el servicio `pricing-test-jacoco` y `pricing-test-pit` definidos en:

```text
docker/docker-compose.yml
```

Ejecutar:

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-jacoco
```

Este comando crea un contenedor temporal utilizando `Dockerfile.jacoco` y ejecuta:

```text
mvn verify
```

## Generar el informe JaCoCo

El servicio `pricing-test-jacoco` monta el directorio local:

```text
docker/jacoco-report/
```

sobre el directorio de JaCoCo dentro del contenedor:

```text
/build/pricing-service/target/site/jacoco
```

Por tanto, al ejecutar:

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-jacoco
```

el informe generado estar&aacute; disponible en:

```text
docker/jacoco-report/
```

El informe HTML principal normalmente ser&aacute;:

```text
docker/jacoco-report/index.html
```

Puedes abrir este fichero en un navegador para consultar.

## Generar el informe Pit

El servicio `pricing-test-pit` monta el directorio local:

```text
docker/pit-report/
```
Al ejecutar:

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-pit
```

Este comando crea un contenedor temporal utilizando `Dockerfile.pit` y ejecuta:

```text
mvn -Pmutation-testing org.pitest:pitest-maven:mutationCoverage
```

el informe generado estar&aacute; disponible en:

```text
docker/pit-report/
```

El informe HTML principal normalmente ser&aacute;:

```text
docker/pit-report/index.html
```

Puedes abrir este fichero en un navegador para consultar.

## Servicios Docker Compose

El fichero `docker/docker-compose.yml` define tres servicios:

### pricing-service

Construye y ejecuta la aplicaci&oacute;n:

```bash
docker compose -f docker/docker-compose.yml up --build pricing-service
```

Utiliza:

```text
docker/Dockerfile
```

y expone el puerto:

```text
8080
```

### pricing-test-jacoco

Ejecuta los tests y genera el informe JaCoCo:

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-jacoco
```

Utiliza:

```text
docker/Dockerfile.jacoco
```

y monta el informe en:

```text
docker/jacoco-report/
```

### pricing-test-pit

Ejecuta los tests y genera el informe pit:

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-pit
```

Utiliza:

```text
docker/Dockerfile.pit
```

y monta el informe en:

```text
docker/pit-report/
```

## Resumen de comandos

### Ejecutar aplicaci&oacute;n

```bash
docker compose -f docker/docker-compose.yml up --build pricing-service
```

### Ejecutar en segundo plano

```bash
docker compose -f docker/docker-compose.yml up --build -d pricing-service
```

### Ejecutar tests + JaCoCo

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-jacoco
```

### Abrir informe JaCoCo

```text
docker/jacoco-report/index.html
```

### Ejecutar pit test

```bash
docker compose -f docker/docker-compose.yml run --rm pricing-test-pit
```

### Abrir informe pit

```text
docker/pit-report/index.html
```

### Detener aplicaci&oacute;n

```bash
docker compose -f docker/docker-compose.yml down
```

## Resumen de Mejoras

- [Eficiencia](IMPROVEMENTS.md#eficiencia)       
- [Testing](IMPROVEMENTS.md#testing)   
- [Control de Versiones](IMPROVEMENTS.md#versions)            
- [Configuraci&oacute;n](IMPROVEMENTS.md#setup)