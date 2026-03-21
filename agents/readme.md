# Agents

This folder holds **Spring Boot agents** that sit on top of the shared mock APIs in [`../mockServices`](../mockServices) (`mockServices`). Each agent calls HTTP endpoints on the mock service and exposes its own JSON API.

**Mock service base URL (default):** `http://localhost:8082` — see `mockServices/src/main/resources/application.properties`. Large mock datasets (`mock_articles.json`, `mock_places.json`, `mock_vessels.json`) can be regenerated from the repository root with:

```bash
python3 mockServices/scripts/generate_mock_data.py
```

| Agent | Port | Package | Role |
|-------|------|---------|------|
| **news-agent** | **8090** | `com.hackathon.newsagent` | Classify news into supply-chain risk categories (multi-label, lexicon). |
| **locations-agent** | **8091** | `com.hackathon.locationsagent` | Resolve **place names → coordinates** (catalog + fuzzy matching). |
| **vessel-agent** | **8092** | `com.hackathon.vesselagent` | List **vessels near a point** using lat/lon + **radius (km)**. |
| **reasoning-agent** | **8093** | `com.hackathon.reasoningagent` | **Orchestrates** news → locations → vessels: classified news, geo resolution, nearby ships. |

**Mock endpoints used**

| Mock path | Method | Used by |
|-----------|--------|---------|
| `/api/v1/article/getArticles` | `POST` | news-agent |
| `/api/v1/places` | `GET` | locations-agent, reasoning-agent (mention scan) |
| `/api/vessels_operations/get-vessels-by-area` | `POST` | vessel-agent (`latitude`, `longitude`, `circle_radius` in km) |

---

## Shared prerequisites

- **Java 21**
- **Maven** (or `./mvnw` from `mockServices` when the wrapper is configured)

### Run the mock API first

From the repository root:

```bash
cd mockServices && mvn spring-boot:run
```

### Port map (local defaults)

| Port | Process |
|------|---------|
| **8082** | `mockServices` (news, places catalog, vessels, places-by-area, …) |
| **8090** | news-agent |
| **8091** | locations-agent |
| **8092** | vessel-agent |
| **8093** | reasoning-agent |

Start each agent you need in **separate terminals**. **reasoning-agent** depends on **mockServices** plus **news-agent**, **locations-agent**, and **vessel-agent** all being up before it.

### Run the full stack (smoke test)

In five terminals from the repo root (after **8082** is healthy, e.g. `curl -sf http://localhost:8082/api/v1/places`):

```bash
cd mockServices && mvn spring-boot:run
cd agents/news-agent && mvn spring-boot:run
cd agents/locations-agent && mvn spring-boot:run
cd agents/vessel-agent && mvn spring-boot:run
cd agents/reasoning-agent && mvn spring-boot:run
```

Quick checks:

```bash
curl -sf "http://localhost:8082/api/v1/places" | head -c 80 && echo
curl -sf "http://localhost:8090/api/agent/classified-news" | head -c 80 && echo
curl -sf "http://localhost:8091/api/agent/resolve-location?name=Dubai%20City" | head -c 80 && echo
curl -sf "http://localhost:8092/api/agent/vessels-nearby?latitude=45.05&longitude=-8.9&radiusKm=50" | head -c 80 && echo
curl -sf "http://localhost:8093/api/agent/reasoning-report" | head -c 80 && echo
```

**reasoning-agent note:** `catalogMentions` is filled only when a **catalog place name** appears in an article’s **title or body** (substring match). Many mock articles mention cities in the title but not the full catalog string in the body, so **zero mentions** for an article is normal. Vessel lists can be **empty** if no mock ships fall within `reasoning.pipeline.search-radius-km` of a resolved point.

---

## news-agent

Supply-chain **news classification**: pulls articles from the mock news API, runs a weighted lexicon over title + body, returns **multi-label** categories with scores and matched keyword signals. Each article in the response includes **`body`** text so downstream agents (e.g. reasoning-agent) can extract geography.

### Run

```bash
cd agents/news-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `news.api.base-url` | `http://localhost:8082` | Mock service base URL |
| `news.api.path` | `/api/v1/article/getArticles` | Article fetch (`POST`) |
| `server.port` | `8090` | Agent port |
| `agent.classification.min-score` | `0.12` | Minimum category score (0–1) to keep a label |
| `agent.classification.max-categories-per-article` | `4` | Max categories per article |

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/classified-news` | Classify all articles from the news API |

```bash
curl -s "http://localhost:8090/api/agent/classified-news" | python3 -m json.tool
```

If the news API is unreachable: **503** with `{ "message": "..." }`.

### Response (summary)

- `articleCount`, `articles[]` with `uri`, `title`, **`body`**, `url`, `date`, `dateTime`, `categories[]`
- Each category: `categoryId`, `categoryLabel`, `categoryDescription`, `score`, `matchedSignals`

Themes include geopolitical risk, trade/tariffs, disasters, logistics disruption, raw materials, cyber, and corporate restructuring. Rules live in `ArticleClassifier`.

### Build

```bash
cd agents/news-agent && mvn -q compile
```

---

## locations-agent

**Location name → coordinates** using the mock catalog (`GET /api/v1/places`). Matching: **exact** name, **substring**, **token overlap** (Jaccard), **Levenshtein**-style fuzzy score. Responses include `matchKind` and `confidence` (0–1).

### Run

```bash
cd agents/locations-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `locations.api.base-url` | `http://localhost:8082` | Mock service base URL |
| `locations.api.catalog-path` | `/api/v1/places` | Catalog (`GET`) |
| `server.port` | `8091` | Agent port |
| `locations.resolve.min-confidence` | `0.55` | Minimum score for a non-exact match |

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/resolve-location?name=...` | Single lookup; **404** if no match |
| `POST` | `/api/agent/resolve-locations` | Body: `{ "queries": ["…"] }` — `resolved` may be `null` |
| `POST` | `/api/agent/refresh-catalog` | Reload catalog from the mock service |

```bash
curl -s "http://localhost:8091/api/agent/resolve-location?name=Dubai%20City"
curl -s "http://localhost:8091/api/agent/resolve-locations" \
  -H "Content-Type: application/json" \
  -d '{"queries":["Strait of Hormuz","Panama","NowhereLandXYZ123"]}'
```

Sample match:

```json
{
  "query": "Dubai City",
  "matchedName": "Dubai City",
  "placeType": "CITY",
  "latitude": 25.26,
  "longitude": 55.3,
  "matchKind": "EXACT",
  "confidence": 1.0
}
```

If the catalog cannot be loaded: **503** with `{ "message": "..." }`.

### Build

```bash
cd agents/locations-agent && mvn -q compile
```

---

## vessel-agent

**Vessels near a point**: send **WGS84** `latitude` / `longitude` and a **search radius in km**. The agent calls `POST .../get-vessels-by-area` with `circle_radius` set to that radius; the mock service keeps vessels whose positions are within the Haversine distance (km).

Vessel positions in JSON are **strings** (`latitude` / `longitude`), consistent with the mock API.

### Run

```bash
cd agents/vessel-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `vessels.api.base-url` | `http://localhost:8082` | Mock service base URL |
| `vessels.api.path` | `/api/vessels_operations/get-vessels-by-area` | Vessel search (`POST`) |
| `server.port` | `8092` | Agent port |
| `vessels.search.default-radius-km` | `100` | Used when `radiusKm` is omitted |

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/vessels-nearby` | Query: `latitude`, `longitude`, optional `radiusKm` |

```bash
curl -s "http://localhost:8092/api/agent/vessels-nearby?latitude=45.05&longitude=-8.9&radiusKm=50"
```

Example response (truncated):

```json
{
  "latitude": 45.05,
  "longitude": -8.9,
  "radiusKm": 50.0,
  "vesselCount": 4,
  "vessels": [
    {
      "mmsi": "245297000",
      "name": "ELBEBORG",
      "latitude": "45.078705",
      "longitude": "-8.9553804",
      "speed": "110",
      "course": "185",
      "heading": "184"
    }
  ]
}
```

Invalid coordinates or non-positive `radiusKm`: **400**. Mock vessel API unreachable: **503** with `{ "message": "..." }`.

### Build

```bash
cd agents/vessel-agent && mvn -q compile
```

---

## reasoning-agent

**End-to-end pipeline** (no extra ML here):

1. **news-agent** — `GET /api/agent/classified-news` (articles with **title**, **body**, risk **categories**).
2. **Place mention scan** — loads `GET /api/v1/places` (mock) and finds catalog **place names** that appear as substrings in title + body (longest names checked first to reduce noise).
3. **locations-agent** — for each distinct mention, `GET /api/agent/resolve-location?name=…` to obtain coordinates.
4. **vessel-agent** — for each **unique** resolved coordinate, `GET /api/agent/vessels-nearby` with `reasoning.pipeline.search-radius-km` (deduplicates multiple place names that map to the same point).

Requires **mockServices** plus **news**, **locations**, and **vessel** agents running. Returns **503** if any upstream HTTP call fails.

### Run

```bash
cd agents/reasoning-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `reasoning.upstream.news-agent-base-url` | `http://localhost:8090` | news-agent |
| `reasoning.upstream.locations-agent-base-url` | `http://localhost:8091` | locations-agent |
| `reasoning.upstream.vessel-agent-base-url` | `http://localhost:8092` | vessel-agent |
| `reasoning.upstream.places-catalog-url` | `http://localhost:8082/api/v1/places` | Mock catalog for substring mention detection |
| `reasoning.pipeline.search-radius-km` | `100` | Radius passed to vessel-agent |
| `server.port` | `8093` | reasoning-agent port |

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/reasoning-report` | Full pipeline JSON |

```bash
curl -s "http://localhost:8093/api/agent/reasoning-report" | python3 -m json.tool
```

Each item in `articles[]` includes `classified` (same fields as news-agent, including **`body`**), `catalogMentions`, `resolvedLocations`, and `vesselsNearLocations` (per distinct coordinate used for a vessel search).

### Web UI (React)

A Vite + React dashboard in [`../reasoning-ui`](../reasoning-ui) (repo root) calls the same report. With **reasoning-agent** on **8093** and **Node.js** installed:

```bash
cd reasoning-ui && npm install && npm run dev
```

Open **http://localhost:5173** — the dev server **proxies** `/api` to `http://localhost:8093`. For `npm run preview` after a production build, either set **`VITE_API_BASE=http://localhost:8093`** when building, or rely on **CORS** (allowed for localhost ports **5173** and **4173** on the reasoning agent).

### Build

```bash
cd agents/reasoning-agent && mvn -q compile
```

---

## Project layout

```
agents/
├── readme.md
├── news-agent/
│   ├── pom.xml
│   └── src/main/java/com/hackathon/newsagent/...
├── locations-agent/
│   ├── pom.xml
│   └── src/main/java/com/hackathon/locationsagent/...
├── vessel-agent/
│   ├── pom.xml
│   └── src/main/java/com/hackathon/vesselagent/...
└── reasoning-agent/
    ├── pom.xml
    └── src/main/java/com/hackathon/reasoningagent/...
```
