# Stage 1: Build
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/customer-*.jar customer-service.jar

# Application user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "customer-service.jar"]