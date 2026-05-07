# Ticketing Microservices Milestone 2 Part 2

This repository contains the completed Milestone 2 Part 2 submission for the ticketing microservices system:

- `api-gateway`
- `customers-service`
- `event-service`
- `ticketinventory-service`
- `ticketordermanagement-service`

The project now includes:

- dedicated `application-testing.yml` profiles
- H2-backed automated tests for business services
- repository, domain, service, controller, adapter, and gateway tests
- JaCoCo report generation for every service
- Docker Compose for the full system

## Architecture

- `customers-service`: customer CRUD and eligibility
- `event-service`: event CRUD, activation/cancellation, eligibility
- `ticketinventory-service`: ticket inventory CRUD and availability checks
- `ticketordermanagement-service`: orchestrator that validates customer, event, and inventory before saving orders
- `api-gateway`: routes `/api/customers/**`, `/api/events/**`, `/api/ticket-inventories/**`, and `/api/ticket-orders/**`

## Automated Test Commands

Run the full milestone command from the repo root:

```bash
./gradlew clean test jacocoTestReport
```

Run one service at a time if needed:

```bash
./gradlew :customers-service:test :customers-service:jacocoTestReport
./gradlew :event-service:test :event-service:jacocoTestReport
./gradlew :ticketinventory-service:test :ticketinventory-service:jacocoTestReport
./gradlew :ticketordermanagement-service:test :ticketordermanagement-service:jacocoTestReport
./gradlew :api-gateway:test :api-gateway:jacocoTestReport
```

## JaCoCo Reports

After running the test command, open these HTML reports:

- `api-gateway/build/reports/jacoco/test/html/index.html`
- `customers-service/build/reports/jacoco/test/html/index.html`
- `event-service/build/reports/jacoco/test/html/index.html`
- `ticketinventory-service/build/reports/jacoco/test/html/index.html`
- `ticketordermanagement-service/build/reports/jacoco/test/html/index.html`

## Docker Compose

Build and start the full system:

```bash
docker compose up --build
```

Stop everything:

```bash
docker compose down
```

The gateway is exposed at `http://localhost:8080`.

## Demo: Docker + Postman

1. Start clean:

```bash
docker compose down -v
docker compose up --build
```

2. Check containers:

```bash
docker compose ps
```

3. Import these files into Postman:

- `postman/ticketing-microservices-demo.postman_collection.json`
- `postman/local-docker.postman_environment.json`

4. Select the `local-docker` environment.

5. Run the full collection from top to bottom.

6. Expected result:

- gateway health returns `UP`
- success and failure requests pass through `http://localhost:8080`
- seeded Docker demo data is available after every fresh `docker compose down -v` followed by `docker compose up --build`

Optional Newman command if Newman is installed:

```bash
newman run postman/ticketing-microservices-demo.postman_collection.json -e postman/local-docker.postman_environment.json
```

## Suggested End-to-End Flow Through the Gateway

1. Create a customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"emailAddress\":\"jane@example.com\",\"phoneNumber\":\"5145551212\"}"
```

2. Create an event

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Jazz Night\",\"description\":\"Live concert\",\"venueName\":\"Place des Arts\",\"venueAddress\":\"175 Sainte-Catherine St W\",\"venueCity\":\"Montreal\",\"startDateTime\":\"2026-06-01T19:00:00\",\"endDateTime\":\"2026-06-01T22:00:00\",\"totalCapacity\":200}"
```

3. Activate the event

```bash
curl -X POST http://localhost:8080/api/events/{eventId}/activate
```

4. Create ticket inventory for that event

```bash
curl -X POST http://localhost:8080/api/ticket-inventories \
  -H "Content-Type: application/json" \
  -d "{\"eventId\":\"{eventId}\",\"ticketType\":\"VIP\",\"totalTickets\":100,\"availableTickets\":100}"
```

5. Create a ticket order

```bash
curl -X POST http://localhost:8080/api/ticket-orders \
  -H "Content-Type: application/json" \
  -d "{\"customerId\":1,\"eventId\":\"{eventId}\",\"ticketType\":\"VIP\",\"quantity\":2,\"totalPrice\":199.98,\"status\":\"PENDING\"}"
```

6. Fetch the created order

```bash
curl http://localhost:8080/api/ticket-orders/{orderId}
```

## Test Coverage Structure

The repository includes the required milestone layers:

- context-load tests
- domain unit tests
- repository integration tests
- service Mockito tests
- controller happy-path tests
- controller negative-path tests
- orchestrator adapter tests
- gateway route and WebTestClient tests

## Notes

- Automated tests use the `testing` profile and do not require Docker or PostgreSQL.
- Runtime Docker Compose uses PostgreSQL for each business service.
- The orchestrator service validates downstream eligibility before persisting ticket orders.
