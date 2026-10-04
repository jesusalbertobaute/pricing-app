# Mejoras

## Eficiencia

### An&aacute;lisis

Se analiz&oacute; la consulta SQL generada por JPA sobre la base de datos H2 con el objetivo de evaluar la eficiencia de la extracci&oacute;n de precios.

#### Consulta del Repository

```java
@Query("""
    SELECT p
    FROM PriceEntity p
    WHERE p.brandId = :brandId
      AND p.productId = :productId
      AND p.startDate <= :applicableDate
      AND p.endDate >= :applicableDate
    ORDER BY p.priority DESC,
             p.createdAt DESC,
             p.id
    """)
List<PriceEntity> findPrices(
    @Param("brandId") Integer brandId,
    @Param("productId") Long productId,
    @Param("applicableDate") LocalDateTime applicableDate,
    Pageable pageable);
```

#### Consulta SQL generada

La consulta SQL equivalente generada por Hibernate es:

```sql
SELECT
    P.ID,
    P.BRAND_ID,
    P.START_DATE,
    P.END_DATE,
    P.TARIFF_ID,
    P.PRODUCT_ID,
    P.PRIORITY,
    P.PRICE,
    P.CURRENCY_CODE,
    P.CREATED_AT
FROM PUBLIC.PRICES P
WHERE P.BRAND_ID = ?
  AND P.PRODUCT_ID = ?
  AND P.START_DATE <= ?
  AND P.END_DATE >= ?
ORDER BY P.PRIORITY DESC,
         P.CREATED_AT DESC,
         P.ID
FETCH FIRST ? ROWS ONLY;
```

El servicio utiliza `PageRequest.of(0, 1)`, por lo que Hibernate incorpora `FETCH FIRST 1 ROWS ONLY`. De esta forma, aunque existan m&uacute;ltiples precios aplicables, la base de datos &uacute;nicamente devuelve el primer resultado necesario.

#### Evaluaci&oacute;n de &iacute;ndices

Se utiliz&oacute; un conjunto de datos sint&eacute;tico para evaluar diferentes estrategias de indexaci&oacute;n. El dataset contiene un volumen general de aproximadamente 100.000 registros y se a&ntilde;adieron registros adicionales para generar un escenario con un elevado n&uacute;mero de candidatos para la combinaci&oacute;n:

```text
brand_id = 1
product_id = 10001
```

Para la misma consulta se evaluaron, de forma independiente, los siguientes &iacute;ndices:

```sql
(BRAND_ID, PRODUCT_ID)

(BRAND_ID, PRODUCT_ID, START_DATE)

(BRAND_ID, PRODUCT_ID, END_DATE)
```

Para cada configuraci&oacute;n se obtuvo el plan de ejecuci&oacute;n mediante `EXPLAIN ANALYZE`.

Adem&aacute;s, antes de realizar las mediciones se ejecut&oacute; un **warm-up de 200 consultas**, con el objetivo de reducir el efecto de la inicializaci&oacute;n y del calentamiento de la aplicaci&oacute;n y de la JVM.

Posteriormente, se realizaron **150 ejecuciones automatizadas** de la misma consulta para cada &iacute;ndice, registrando el tiempo de respuesta mediante `System.nanoTime()`.

##### Resultados

| &iacute;ndice                               |  scanCount |        Media |          P95 |          P99 |
| ------------------------------------ | ---------: | -----------: | -----------: | -----------: |
| `(BRAND_ID, PRODUCT_ID)`             |     40.001 |     0,766 ms |     1,332 ms |     1,780 ms |
| `(BRAND_ID, PRODUCT_ID, START_DATE)` | **25.886** | **0,476 ms** | **0,884 ms** | **1,216 ms** |
| `(BRAND_ID, PRODUCT_ID, END_DATE)`   |     34.060 |     0,602 ms |     0,995 ms |     1,582 ms |

El &iacute;ndice `(BRAND_ID, PRODUCT_ID, START_DATE)` permite reducir el n&uacute;mero de registros examinados de 40.001 a 25.886, aproximadamente un **35 % menos**.

Esta reducci&oacute;n tambi&eacute;n se refleja en el tiempo medio de ejecuci&oacute;n, que pasa de 0,766 ms a 0,476 ms, lo que supone aproximadamente un **38 % de reducci&oacute;n** en este escenario.

Asimismo, se observa una mejora en los percentiles de latencia:

* **P95:** de 1,332 ms a 0,884 ms.
* **P99:** de 1,780 ms a 1,216 ms.

En pruebas adicionales realizadas con diferentes fechas de consulta se observaron resultados similares, con una mayor selectividad del &iacute;ndice que incorpora `START_DATE` para la distribuci&oacute;n de datos utilizada.

##### Conclusi&oacute;n

A partir de las mediciones realizadas, se decidi&oacute; utilizar el siguiente &iacute;ndice compuesto:

```sql
CREATE INDEX IDX_PRICES_BRAND_PRODUCT_START_DATE
ON PRICES (BRAND_ID, PRODUCT_ID, START_DATE);
```

La decisi&oacute;n se basa en que, para la distribuci&oacute;n de datos y los patrones de consulta evaluados, este &iacute;ndice reduce tanto el n&uacute;mero de registros examinados como el tiempo de respuesta de la consulta.

No obstante, esta conclusi&oacute;n depende de la distribuci&oacute;n de los datos y del patr&oacute;n de consultas. Una distribuci&oacute;n diferente podr&iacute;a hacer que `END_DATE` resultase m&aacute;s selectivo. Por este motivo, el &iacute;ndice seleccionado no debe considerarse universalmente más eficiente que cualquier alternativa, sino como la opci&oacute;n que present&oacute; mejores resultados en el escenario evaluado.

Finalmente, la incorporaci&oacute;n de un &iacute;ndice adicional implica un coste de mantenimiento durante operaciones de escritura (`INSERT`, `UPDATE` y `DELETE`), por lo que la decisi&oacute;n debe considerar tanto la frecuencia de las consultas de lectura como el volumen y frecuencia de las operaciones de escritura en ambiente productivo.

## Testing

A partir de la revisión, se identificó que la estrategia de testing presentaba oportunidades de mejora.

La mejora aplicada aborda lo siguiente:

* Se han incorporado tests unitarios para las responsabilidades de aplicación y servicio.
* Se han incorporado tests de integración sobre el repositorio utilizando H2 para validar las reglas que dependen de la persistencia, especialmente la selección por fecha y prioridad.
* Se han reforzado los tests REST para validar no solo el código HTTP, sino también el contenido completo de la respuesta.
* Se han cubierto los escenarios funcionales definidos para la selección de precios, incluyendo casos de límites y ausencia de resultados.
* Se han utilizado tests parametrizados cuando diferentes entradas representan la misma regla de negocio, reduciendo duplicación y facilitando el mantenimiento.
* Se ha incorporado JaCoCo como mecanismo para identificar código no ejecutado y posibles zonas sin cobertura.
* Se ha incorporado PIT como complemento para comprobar la efectividad real de los tests, verificando si son capaces de detectar modificaciones en el comportamiento del código.

De esta forma, la estrategia de testing se hace más completa.

Los tests unitarios validan el comportamiento aislado, los tests de integración validan la interacción con la persistencia y las reglas implementadas en ella, y los tests REST validan el comportamiento observable por el consumidor de la API.

Por tanto, la mejora realizada permite responder de forma más robusta a los problemas inicialmente detectados en el testing, aumentando la confianza en que una modificación del código pueda ser detectada por la suite de pruebas.

## <a id="versions"></a>Control de Versiones

Como mejora del proceso de desarrollo, se ha establecido una estrategia de control de versiones basada en convenciones de nombres, ramas de trabajo, commits estructurados, Pull Requests y versionado independiente de los proyectos.

El objetivo es mejorar la **trazabilidad, mantenibilidad y claridad del historial de cambios**, siguiendo prácticas habituales de trabajo con Git.

### Branching strategy

Se utiliza una estrategia basada en ramas con responsabilidades diferenciadas:

| Branch       | Propósito                                                                        |
| ------------ | -------------------------------------------------------------------------------- |
| `main`       | Contiene las versiones estables y releases del proyecto.                         |
| `develop`    | Rama de integración de los cambios antes de una release.                         |
| `feature/*`  | Desarrollo de nuevas funcionalidades o cambios de comportamiento.                |
| `refactor/*` | Mejoras estructurales o de calidad sin modificar el comportamiento esperado.     |
| `test/*`     | Incorporación o mejora de pruebas.                                               |
| `docs/*`     | Cambios exclusivamente relacionados con documentación.                           |
| `release/*`  | Preparación de una versión concreta para su publicación.                         |

Las ramas de trabajo se crean a partir de `develop` y, una vez finalizado el trabajo, se integran mediante Pull Requests.

Las ramas temporales (`feature/*`, `refactor/*`, `test/*` etc.) se eliminan después de su integración. La eliminación de la rama no elimina los commits que ya forman parte del historial de `develop` o `main`.

### Flujo de cambios

El flujo habitual para un cambio es:

```text
develop
   │
   ├── feature/* / refactor/* / test/* / docs/*
   │
   └── Pull Request
          │
          ▼
       develop
```

Los cambios se desarrollan de forma aislada en una rama específica y se integran mediante Pull Request después de realizar las validaciones correspondientes.

Para las releases se utiliza una rama específica:

```text
develop
   │
   └── release/<project>-<version>
             │
             ├── actualización de versión
             ├── validación
             └── Pull Request
                    │
                    ▼
                  main
                    │
                    └── tag de release
```

Este flujo permite separar el desarrollo de nuevas funcionalidades de la preparación de versiones estables.

### Convención de commits

Los commits siguen una convención basada en **Conventional Commits**, utilizando prefijos que describen la naturaleza del cambio.

Ejemplos:

```text
feat(api): update error response contract
fix(service): handle missing price
perf(repository): optimize price lookup
refactor(api): simplify exception handling
test(api): add error response tests
docs: document versioning strategy
ci: add Maven verification workflow
chore(release): prepare pricing-api-contract 2.0.0
```

Esto permite identificar rápidamente la intención de cada cambio y facilita la revisión del historial.

### Versionado independiente

El repositorio contiene dos proyectos Maven independientes:

```text
pricing-api-contract
pricing-service
```

Cada proyecto mantiene su propio ciclo de versiones y no se incrementa automáticamente la versión de un proyecto cuando cambia el otro.

Por ejemplo:

```text
pricing-api-contract  → 2.0.0
pricing-service   → 2.0.0
```

Esto permite que cada componente evolucione según el impacto de sus propios cambios.

Para determinar el incremento de versión se utiliza Semantic Versioning:

```text
MAJOR.MINOR.PATCH
```

* **MAJOR**: cambios incompatibles con versiones anteriores.
* **MINOR**: nuevas funcionalidades compatibles.
* **PATCH**: correcciones compatibles.

Por ejemplo, los cambios introducidos en `pricing-api-contract` que modifican el contrato público y la URL del endpoint constituyen cambios incompatibles, por lo que se prepara la versión:

```text
pricing-api-contract 2.0.0
```

El hecho de que `pricing-api-contract` pase a `2.0.0` no implica que `pricing-service` tenga que pasar también a `2.0.0`.

### Release branches

Las releases se preparan mediante ramas específicas por proyecto y versión.

Por ejemplo:

```text
release/pricing-api-contract-2.0.0
```

En esta rama se realizan únicamente las tareas necesarias para preparar la release, como:

* actualización de la versión Maven;
* validación de tests;
* ejecución de `mvn clean verify`;
* actualización de documentación de release;
* comprobaciones finales antes de integrar la versión en `main`.

Una vez validada, la rama se integra en `main` mediante Pull Request.

### Tags de release

Cada versión publicada queda identificada mediante un tag de git asociado al proyecto y a su versión.

Ejemplo:

```text
pricing-api-contract-v2.0.0
```

Posteriormente, una nueva release del otro proyecto podría utilizar:

```text
pricing-service-v2.1.0
```

El uso del nombre del proyecto en el tag evita ambig&uuml;edades, ya que el repositorio contiene varios proyectos Maven con versiones independientes.

El tag representa el estado exacto del código correspondiente a una versión publicada y permite recuperar o identificar posteriormente el código asociado a dicha release.

### Flujo completo

El proceso completo queda establecido de la siguiente forma:

```text
                 ┌──────────────┐
                 │    develop   │
                 └──────┬───────┘
                        │
                 crear rama de trabajo
                        │
                        ▼
              ┌────────────────────┐
              │ feature/           │
              │ refactor/test/...  │
              └─────────┬──────────┘
                        │
                   Pull Request
                        │
                        ▼
                 ┌──────────────┐
                 │    develop   │
                 └──────┬───────┘
                        │
                 preparar release
                        │
                        ▼
          ┌───────────────────────────┐
          │ release/<project>-<version>│
          └─────────────┬─────────────┘
                        │
                   validaciones
                        │
                   Pull Request
                        │
                        ▼
                 ┌──────────────┐
                 │     main     │
                 └──────┬───────┘
                        │
                    crear tag
                        │
                        ▼
          <project>-v<version>
```

### Resultado de la mejora

Con esta estrategia se establece un proceso de control de versiones más estructurado y trazable, utilizando:

* Convenciones estandarizadas para nombres de ramas;
* Conventional Commits;
* Pull Requests para integración de cambios;
* Separación entre desarrollo, integración y releases;
* Semantic Versioning;
* Versionado independiente de cada proyecto Maven;
* Release branches específicas por proyecto y versión;
* Tags identificativos para cada versión publicada.

De esta forma, cada cambio puede trazarse desde su rama y commits hasta su integración, release y tag correspondiente.

## <a id="setup"></a>Configuración

Para abordar los errores y *warnings* detectados por el **linter**, se incorporó **Spectral** como mecanismo de validación y *governance* del contrato OpenAPI.

Además de corregir problemas de calidad del contrato, se definieron reglas arquitectónicas como el **versionado mediante `/v{number}`**, permitiendo gestionar de forma explícita la evolución del API y los posibles *breaking changes*.