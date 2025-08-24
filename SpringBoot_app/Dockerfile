# ============================
# Etap 1: dependencies
# ============================
FROM maven:3.9.2-eclipse-temurin-20 AS dependencies
WORKDIR /opt/app

# Kopiujemy wszystkie POM-y i źródła modułów minimalnie, żeby Maven mógł pobrać zależności
COPY pom.xml .
COPY aTSPSecurity/pom.xml aTSPSecurity/pom.xml
COPY databaseCore/pom.xml databaseCore/pom.xml
COPY mapAuthRest/pom.xml mapAuthRest/pom.xml
COPY mapAuthRestService/pom.xml mapAuthRestService/pom.xml
COPY userAuthRest/pom.xml userAuthRest/pom.xml
COPY userAuthRestService/pom.xml userAuthRestService/pom.xml

# Tworzymy minimalne puste foldery src, żeby Maven nie narzekał
RUN mkdir -p aTSPSecurity/src databaseCore/src mapAuthRest/src mapAuthRestService/src userAuthRest/src userAuthRestService/src

# Pobieramy zależności offline
RUN mvn dependency:go-offline -B


# ============================
# Etap 2: builder
# ============================
FROM maven:3.9.2-eclipse-temurin-20 AS builder
WORKDIR /opt/app

# Kopiujemy repozytorium i POM-y z etapu dependencies
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /opt/app /opt/app

# Kopiujemy wszystkie źródła
COPY aTSPSecurity/src /opt/app/aTSPSecurity/src
COPY databaseCore/src /opt/app/databaseCore/src
COPY mapAuthRest/src /opt/app/mapAuthRest/src
COPY mapAuthRestService/src /opt/app/mapAuthRestService/src
COPY userAuthRest/src /opt/app/userAuthRest/src
COPY userAuthRestService/src /opt/app/userAuthRestService/src

# Budujemy wszystkie moduły
RUN mvn clean install -DskipTests -B

# ============================
# Etap 3: runtime
# ============================
FROM openjdk:20-slim AS runtime
WORKDIR /opt/app

# Kopiujemy wszystkie gotowe JAR-y
COPY --from=builder /opt/app/aTSPSecurity/target/aTSPSecurity-0.0.1-SNAPSHOT.jar /opt/app/aTSPSecurity.jar
COPY --from=builder /opt/app/mapAuthRest/target/mapAuthRest-0.0.1-SNAPSHOT.jar /opt/app/mapAuthRest.jar
COPY --from=builder /opt/app/mapAuthRestService/target/mapAuthRestService-0.0.1-SNAPSHOT.jar /opt/app/mapAuthRestService.jar
COPY --from=builder /opt/app/userAuthRest/target/userAuthRest-0.0.1-SNAPSHOT.jar /opt/app/userAuthRest.jar
COPY --from=builder /opt/app/userAuthRestService/target/userAuthRestService-0.0.1-SNAPSHOT.jar /opt/app/userAuthRestService.jar