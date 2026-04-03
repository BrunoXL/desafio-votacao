# ─────────────────────────────────────────
# STAGE 1: Build com Corretto 25 + Maven instalado manualmente
# ─────────────────────────────────────────
FROM amazoncorretto:25-alpine3.23 AS builder

# Instala Maven e dependências necessárias no Alpine
RUN apk add --no-cache curl tar bash

ARG MAVEN_VERSION=3.9.14
RUN curl -fsSL -o /tmp/maven.tar.gz \
      https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzC /opt -f /tmp/maven.tar.gz && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven && \
    rm /tmp/maven.tar.gz

ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

ENV PATH="/opt/maven/bin:${PATH}"
ENV MAVEN_HOME=/opt/maven

WORKDIR /app

# Baixa dependências primeiro (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código e builda
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────
# STAGE 2: Runtime com Corretto 25 + Alpine 3.23
# ─────────────────────────────────────────
FROM amazoncorretto:25-alpine3.23

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]