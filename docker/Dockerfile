FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pricing-api-contract/pom.xml pricing-api-contract/pom.xml

RUN mvn \
    -f pricing-api-contract/pom.xml \
    clean install \
    -DskipTests

COPY pricing-api-contract/target/classes/pricing-api/ \
     /build/pricing-api-contract/target/classes/pricing-api/    

COPY pricing-service/pom.xml pricing-service/pom.xml
COPY pricing-service/src pricing-service/src

RUN mvn \
    -f pricing-service/pom.xml \
    clean \
    -DskipTests

RUN mkdir -p /build/pricing-service/target/contract/pricing-api \
    && cp -r /build/pricing-api-contract/target/classes/pricing-api/. \
       /build/pricing-service/target/contract/pricing-api/

RUN mvn \
    -f pricing-service/pom.xml \
    package \
    -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/pricing-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]