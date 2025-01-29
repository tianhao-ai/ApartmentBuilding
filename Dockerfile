# Build stage
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:17-slim

# Install X11 and font libraries
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libfreetype6 \
    libfontconfig1 \
    fonts-dejavu \
    x11-utils \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/build/libs/apartment-building.jar app.jar

# Set environment variables for GUI
ENV JAVA_TOOL_OPTIONS="-Dsun.java2d.xrender=false -Dsun.java2d.opengl=false"

ENTRYPOINT ["java", "-jar", "app.jar"] 