# Ticketing Microservices

## Project overview

This project is a Spring Boot microservices system for a Web Services course. It includes four business services and one API Gateway. For local IntelliJ development, the services use in-memory H2 databases. For Docker, the services run with PostgreSQL and are exposed through Spring Cloud Gateway on port `8080`.

## Services and ports

| Service | Local Port | Docker Service Name |
| --- | --- | --- |
| API Gateway | `8080` | `api-gateway` |
| Customers Service | `8081` | `customers-service` |
| Event Service | `8082` | `event-service` |
| Ticket Inventory Service | `8083` | `ticketinventory-service` |
| Ticket Order Management Service | `8084` | `ticketordermanagement-service` |

## Local IntelliJ run instructions using H2

1. Open the Gradle root project in IntelliJ.
2. Run each Spring Boot application from IntelliJ with the default profile:
   - `customers-service`
   - `event-service`
   - `ticketinventory-service`
   - `ticketordermanagement-service`
   - `api-gateway`
3. Do not set `SPRING_PROFILES_ACTIVE` locally unless you want Docker settings.
4. The services will use H2 by default and listen on:
   - `8081`, `8082`, `8083`, `8084`
5. Access the system through the gateway on `http://localhost:8080`.

## Docker run instructions using PostgreSQL

1. From the project root, build everything:

```bash
./gradlew clean build
```

2. Start Docker Compose:

```bash
docker compose up --build
```

3. Docker uses the `docker` Spring profile for every service, so the apps connect to PostgreSQL containers and the gateway routes by Docker service name.
4. Access the system through `http://localhost:8080`.
5. Docker starts 9 containers total:
   - 4 PostgreSQL containers
   - 4 business-service containers
   - 1 API Gateway container

## Gateway URLs through localhost:8080

- `http://localhost:8080/api/customers/**`
- `http://localhost:8080/api/events/**`
- `http://localhost:8080/api/ticket-inventories/**`
- `http://localhost:8080/api/ticket-orders/**`

## Swagger URLs for each service

- API Gateway: `http://localhost:8080/swagger-ui.html`
- Customers Service: `http://localhost:8081/swagger-ui.html`
- Event Service: `http://localhost:8082/swagger-ui.html`
- Ticket Inventory Service: `http://localhost:8083/swagger-ui.html`
- Ticket Order Management Service: `http://localhost:8084/swagger-ui.html`

## Postman demo checklist

- Confirm all five applications start successfully.
- Confirm the gateway responds on `http://localhost:8080`.
- Send a request through `GET /api/customers`.
- Send a request through `GET /api/events`.
- Send a request through `GET /api/ticket-inventories`.
- Send a request through `GET /api/ticket-orders`.
- Create at least one sample record where applicable using `POST` endpoints.
- Confirm each service Swagger UI loads.
- If using Docker, confirm all 9 containers are running before the demo.
