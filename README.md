# SpringCloud-Bus-Kafka Project

## Overview
This project demonstrates the usage of Spring Cloud Bus backed by an Apache Kafka message broker, along with Spring Cloud Config and Spring Boot Actuator. The primary goal of this application is to showcase how to achieve dynamic configuration updates across multiple microservices simultaneously using a lightweight message broker, without requiring application restarts.

## Technologies Used and Why

### 1. Spring Boot 3.5.7
- **Why**: Provides the foundational framework for building the microservice. It allows for rapid application development with built-in auto-configuration, embedded servers (like Tomcat), and simplified dependency management.

### 2. Spring Cloud Config (Client)
- **Why**: Used for externalizing the configuration. The application fetches its properties (like `balance` and `username`) from a centralized Spring Cloud Config Server (`http://localhost:8763`). This enables centralized management of properties across various environments.

### 3. Spring Cloud Bus with Apache Kafka
- **Why**: This is the core component of this project. 
  - **Spring Cloud Bus** links nodes of a distributed system with a lightweight message broker.
  - **Apache Kafka** acts as the message broker (`localhost:9092`).
  - **Use Case**: When a configuration changes in the centralized Git repository (backing the Config Server), you only need to hit the `/actuator/busrefresh` endpoint on *one* of the instances. Spring Cloud Bus will broadcast a `RefreshRemoteApplicationEvent` over the Kafka topic. All other microservices subscribed to this topic will consume the event and refresh their configurations dynamically. This eliminates the need to hit `/actuator/refresh` on every single instance manually.

### 4. Spring Boot Actuator
- **Why**: Provides production-ready features to help monitor and manage the application. 
  - Specifically, we exposed the `busrefresh` endpoint (`management.endpoints.web.exposure.include=busrefresh`) via HTTP. This endpoint triggers the configuration refresh event that is broadcasted across the Kafka bus.

### 5. `@RefreshScope` Annotation
- **Why**: Applied to the `TestController`. It forces the Spring container to re-initialize the bean and re-inject the `@Value` properties (`balance` and `username`) when a refresh event is triggered, ensuring the application uses the latest values from the Config Server without a restart.

## Important Configurations (`application.properties`)

- `spring.config.import=configserver:http://localhost:8763`: Tells the application where to find the Config Server.
- `spring.cloud.config.retry.*`: Implements a fail-fast and retry mechanism in case the Config Server is not immediately available during startup.
- `spring.kafka.bootstrap-servers=localhost:9092`: Points to the local Kafka broker instance required for Spring Cloud Bus.
- `management.endpoints.web.exposure.include=busrefresh`: Exposes the specific actuator endpoint needed to trigger cluster-wide config refreshes.

## How it Works
1. The application starts up and connects to the Config Server to load its properties.
2. It also connects to the Kafka broker.
3. The `TestController` serves an endpoint `/balance` that displays the current configuration values.
4. If the configuration changes in the remote repository, sending an HTTP POST request to `http://<host>:<port>/actuator/busrefresh` on *any* instance of this application will cause *all* instances to reload their configuration.