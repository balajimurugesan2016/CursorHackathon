# Enterprise service — master data, mock data, orchestration

Three JPA entities — **`Plant`**, **`Supplier`**, **`Shipment`** — with **M:N** between each pair (**`@ManyToMany`** / **`@JoinTable`**). Join tables in the DB have surrogate **`id`**, two FKs, and **`UNIQUE (pair)`**; no separate link **`@Entity`** types. Use **`schema.sql`** or migrations so Hibernate **validates**. This service also aggregates mock **articles**, **vessels**, and **places** from [mockServices](../../mockServices). **Out of scope:** UI, agents, AI.

## Implementation checklist

- [x] Entities, repositories, REST, **`/api/v1/links`** for M:N pairs
- [ ] Extend `mock_places.json`; `Place` / `PlaceController` (distinct from enterprise `Shipment`)
- [ ] Optional keys between enterprise DB and mock APIs
- [ ] Orchestration tuning; config-based mock URLs
- [ ] Optional Docker / Cloud Foundry manifest

## Entity model

Reference image: [reference-entity-model-style.png](assets/reference-entity-model-style.png).

**Owning `@JoinTable`:** **`Plant`** → `plant_supplier`, `plant_shipment`; **`Supplier`** → `supplier_shipment`. **`mappedBy`** on **`Supplier.plants`** (`suppliers`), **`Shipment.suppliers`** (`shipments`), **`Shipment.plants`** (`plantShipments`). Each **`@JoinTable`** has **`@UniqueConstraint`** matching DDL **`uk_*`**.

### Domain (JPA)

```mermaid
classDiagram
  direction LR
  Plant "*" -- "*" Supplier : plant_supplier
  Supplier "*" -- "*" Shipment : supplier_shipment
  Plant "*" -- "*" Shipment : plant_shipment

  class Plant {
    +Long id
    +String plantName
    +String location
    +String latitude
    +String longitude
    +String status
    +BigDecimal capacityPct
    +Integer totalLines
    +Integer linesActive
    +Set~Supplier~ suppliers
    +Set~Shipment~ plantShipments
  }

  class Supplier {
    +Long id
    +String supplierName
    +String location
    +String latitude
    +String longitude
    +String contractStatus
    +Set~Plant~ plants
    +Set~Shipment~ shipments
  }

  class Shipment {
    +Long id
    +String shipmentItem
    +BigDecimal quantity
    +String shipNumber
    +String status
    +Instant receiveDate
    +Set~Supplier~ suppliers
    +Set~Plant~ plants
  }

  style Plant fill:#ececec,stroke:#000,stroke-width:1px
  style Supplier fill:#ececec,stroke:#000,stroke-width:1px
  style Shipment fill:#ececec,stroke:#000,stroke-width:1px
```

### Physical (tables)

`receiveDate` maps to **`Instant`** in Java (`TIMESTAMP WITH TIME ZONE` in H2).

```mermaid
erDiagram
  plant {
    bigint id PK
    varchar plant_name
    varchar location
    varchar latitude
    varchar longitude
    varchar status
    decimal capacity_pct
    int total_lines
    int lines_active
  }
  supplier {
    bigint id PK
    varchar supplier_name
    varchar location
    varchar latitude
    varchar longitude
    varchar contract_status
  }
  shipment {
    bigint id PK
    varchar shipment_item
    decimal quantity
    varchar ship_number
    varchar status
    timestamptz receive_date
  }
  plant_supplier {
    bigint id PK
    bigint plant_id FK
    bigint supplier_id FK
  }
  supplier_shipment {
    bigint id PK
    bigint supplier_id FK
    bigint shipment_id FK
  }
  plant_shipment {
    bigint id PK
    bigint plant_id FK
    bigint shipment_id FK
  }

  plant ||--o{ plant_supplier : ""
  supplier ||--o{ plant_supplier : ""
  supplier ||--o{ supplier_shipment : ""
  shipment ||--o{ supplier_shipment : ""
  plant ||--o{ plant_shipment : ""
  shipment ||--o{ plant_shipment : ""
```

**Persistence:** Join tables hold links only; **`@JoinTable`** in JPA maps the two FK columns — the extra surrogate **`id`** and **`UNIQUE` pair** live in DDL (**`schema.sql`**). REST: link/unlink **[`/api/v1/links/...`](../readme.md)**.

## Runtime architecture

```mermaid
flowchart LR
  subgraph ent [Spring_Enterprise]
    API[REST_JPA]
    DB[(H2_or_PostgreSQL)]
  end
  subgraph mock [mockServices]
    Articles[articles]
    Vessels[vessels]
    Places[places]
  end
  API --> DB
  API --> Orch[Orchestration_layer]
  Orch --> Articles
  Orch --> Vessels
  Orch --> Places
```

Orchestration: parallel **`RestClient`** calls to mock endpoints; stateless API; tune timeouts / resilience as needed.

## Mock layer

[mock_places.json](../../mockServices/src/main/resources/mock_places.json): optional `plantId` / `externalRef`, `shipments[]`; adjust Jackson / mock **`PlaceController`** as needed.

## Risks

- Name clashes (e.g. mock `GeoShipment` vs JPA `Shipment`).
- Wrong **`mappedBy`** → duplicate or conflicting join table definitions.
- Triple M:N can produce many link rows; add app rules if some combinations are invalid.
