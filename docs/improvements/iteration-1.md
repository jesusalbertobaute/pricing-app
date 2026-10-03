# Eficiencia

## An&aacute;lisis

Se analiz&oacute; la consulta SQL generada por JPA sobre la base de datos H2 con el objetivo de evaluar la eficiencia de la extracci&oacute;n de precios.

### Consulta del Repository

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

### Consulta SQL generada

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

## Evaluaci&oacute;n de &iacute;ndices

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

### Resultados

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

## Conclusi&oacute;n

A partir de las mediciones realizadas, se decidi&oacute; utilizar el siguiente &iacute;ndice compuesto:

```sql
CREATE INDEX IDX_PRICES_BRAND_PRODUCT_START_DATE
ON PRICES (BRAND_ID, PRODUCT_ID, START_DATE);
```

La decisi&oacute;n se basa en que, para la distribuci&oacute;n de datos y los patrones de consulta evaluados, este &iacute;ndice reduce tanto el n&uacute;mero de registros examinados como el tiempo de respuesta de la consulta.

No obstante, esta conclusi&oacute;n depende de la distribuci&oacute;n de los datos y del patr&oacute;n de consultas. Una distribuci&oacute;n diferente podr&iacute;a hacer que `END_DATE` resultase m&aacute;s selectivo. Por este motivo, el &iacute;ndice seleccionado no debe considerarse universalmente más eficiente que cualquier alternativa, sino como la opci&oacute;n que present&oacute; mejores resultados en el escenario evaluado.

Finalmente, la incorporaci&oacute;n de un &iacute;ndice adicional implica un coste de mantenimiento durante operaciones de escritura (`INSERT`, `UPDATE` y `DELETE`), por lo que la decisi&oacute;n debe considerar tanto la frecuencia de las consultas de lectura como el volumen y frecuencia de las operaciones de escritura en ambiente productivo.
