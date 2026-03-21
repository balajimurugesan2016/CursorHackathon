# Enterprise service

**Purpose:** Hackathon **master data** for the supply network (**plants**, **suppliers**, **shipments**) plus **orchestration** that pulls mock **news, vessels, and places** into one snapshot for **dashboards**—while specialized **agents** (news, ships, reasoning) live under [`agents/`](../agents). Geography for demos (including **choke points** such as the Strait of Hormuz in mock data) aligns with seeded **plants** via `mock_places.json`.

Design and domain plan: [docs/enterprise-service-plan.md](docs/enterprise-service-plan.md).

## Run

From the **repository root** (`CursorHackathon/`):

1. **Mock APIs** (mockServices), port **8082**:

```bash
cd mockServices
./run.sh
```

2. **Enterprise service**, port **8085** (in another terminal):

```bash
cd enterpriseservice
./run.sh
```

If you are already inside `enterpriseservice/`, use `../mockServices/run.sh` for the mock app.

### Why `run.sh` instead of `mvn spring-boot:run`?

On some SAP Artifactory setups, **`spring-boot-maven-plugin`** cannot resolve **`spring-boot-loader-tools`** / **`spring-boot-buildpack-platform`**, and **`exec-maven-plugin`** may not be hosted either. **`run.sh`** uses only **`mvn compile`** + the standard Apache **`maven-dependency-plugin`** (`dependency:build-classpath`) + **`java`**, which are usually already available.

If your mirror resolves the Spring Boot plugin, you can still use:

```bash
mvn spring-boot:run
```

[`pom.xml`](pom.xml) lists **Maven Central** as an extra `repository` / `pluginRepository`; a global `settings.xml` mirror may still override that.

Service listens on **8085** (see `server.port` in [`application.properties`](src/main/resources/application.properties)). H2 in-memory schema comes from [`src/main/resources/schema.sql`](src/main/resources/schema.sql), then Hibernate **validates** mappings.

**Startup seed (empty DB):** (1) **Plants** from [`classpath:mock_places.json`](src/main/resources/mock_places.json) (aligned with mockServices geography). (2) If the **supplier** table is empty, **suppliers** from [`classpath:mock_supply_partners.json`](src/main/resources/mock_supply_partners.json) and **plant–supplier links** (e.g. Dubai City / Port Jebel Ali → Gulf partner; Strait of Hormuz / Strait of Malacca → corridor partners) for supply-chain risk demos. Order: `@Order(1)` plant seed, `@Order(2)` supply seed.

Override mock base URL: edit [`application.properties`](src/main/resources/application.properties), set env **`MOCK_SERVICES_BASE_URL`**, or with `spring-boot:run`:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--mock.services.base-url=http://localhost:8082
```

## API smoke test (`curl`)

**Base URL:** `http://localhost:8085/api/v1` (restart the app after changing `application.properties`).

**Plants** (repeat for `/suppliers` and `/shipments` with `supplierName` / shipment fields):

```bash
curl -s http://localhost:8085/api/v1/plants
curl -s http://localhost:8085/api/v1/plants/1
curl -s -X POST http://localhost:8085/api/v1/plants -H "Content-Type: application/json" \
  -d '{"plantName":"Alpha","location":"Dubai"}'
curl -s -X PUT http://localhost:8085/api/v1/plants/1 -H "Content-Type: application/json" \
  -d '{"plantName":"Alpha","location":"Abu Dhabi"}'
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE http://localhost:8085/api/v1/plants/1
```

**Links** (M:N — replace ids; `204` expected):

```bash
BASE=http://localhost:8085/api/v1/links
curl -s -o /dev/null -w "%{http_code}\n" -X POST "$BASE/plants/1/suppliers/1"
curl -s -o /dev/null -w "%{http_code}\n" -X POST "$BASE/suppliers/1/shipments/1"
curl -s -o /dev/null -w "%{http_code}\n" -X POST "$BASE/plants/1/shipments/1"
```

**Orchestration** (needs **mockServices** on 8082):

```bash
curl -s "http://localhost:8085/api/v1/orchestration/snapshot?latitude=25.26&longitude=55.30&vesselRadiusKm=50"
```

## H2 console

With the app running: [http://localhost:8085/h2-console](http://localhost:8085/h2-console) — JDBC URL `jdbc:h2:mem:enterprise`, user `sa`, empty password (defaults from `application.properties`).
