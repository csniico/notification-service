FROM ubuntu:25.10 AS builder

RUN apt-get update && \
    apt-get install -y wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_VERSION=21
ENV JAVA_HOME=/opt/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

RUN wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1+12/OpenJDK21U-jdk_x64_linux_hotspot_21.0.1_12.tar.gz -O /tmp/openjdk.tar.gz && \
    mkdir -p /opt/openjdk && \
    tar -xzf /tmp/openjdk.tar.gz --strip-components=1 -C /opt/openjdk && \
    rm /tmp/openjdk.tar.gz

WORKDIR /app

# Download the OpenTelemetry Java agent
ENV OTEL_AGENT_VERSION=1.38.0
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar \
    -O /app/opentelemetry-javaagent.jar

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=builder /app/target/notificationService-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=builder /app/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

RUN echo '#!/bin/sh\n\
java \
-javaagent:/app/opentelemetry-javaagent.jar \
-Dotel.exporter.otlp.traces.endpoint=${OTEL_EXPORTER_OTLP_TRACES_ENDPOINT} \
-Dotel.service.name=${OTEL_SERVICE_NAME} \
-Dotel.metrics.exporter=otlp \
-Dotel.exporter.otlp.metrics.endpoint=${OTEL_EXPORTER_OTLP_METRICS_ENDPOINT:-${OTEL_EXPORTER_OTLP_TRACES_ENDPOINT}} \
-Dotel.traces.exporter=otlp \
-Dotel.propagators=tracecontext,baggage,b3 \
-jar /app/app.jar\
' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]