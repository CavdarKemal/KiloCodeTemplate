# Multi-stage build for optimized Docker image
# Stage 1: Build (using local build, copy only JAR)
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app
COPY target/verein-verwaltung-1.0.0.jar app.jar

# Stage 2: Runtime (JRE only, smaller image)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D appuser

# Copy JAR from builder
COPY --from=builder /app/app.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]