# Agents

This folder holds **Spring Boot agents** that sit on top of the shared mock APIs in [`../mockServices`](../mockServices) (`mockServices`). Each agent calls one or more HTTP endpoints on the mock service, then adds classification, resolution, or other logic and exposes its own JSON API.

| Agent | Package | Port (default) | What it does |
|-------|---------|----------------|--------------|
| **news-agent** | `com.hackathon.newsagent` | **8090** | Fetches news articles, assigns **supply-chain risk categories** (lexicon-based, multi-label). |
| **locations-agent** | `com.hackathon.locationsagent` | **8091** | Maps **place names → latitude/longitude** using the mock place catalog (exact, substring, token overlap, fuzzy). |

The mock service must be running first (default **8082** — see `mockServices/src/main/resources/application.properties`). It exposes endpoints such as:

- `POST /api/v1/article/getArticles` — articles JSON (used by news-agent)
- `GET /api/v1/places` — full place catalog (used by locations-agent)

---

## Shared prerequisites

- **Java 21**
- **Maven** (or `./mvnw` from `mockServices` when the wrapper is configured)

### Run the mock API first

From the repository root:

```bash
cd mockServices && mvn spring-boot:run
```

Then start whichever agent(s) you need in **separate terminals** (see below).

---

## news-agent

Supply-chain **news classification**: pulls articles from the mock news API, runs a weighted lexicon over title + body, returns **multi-label** categories with scores and matched keyword signals.

### Run

```bash
cd agents/news-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `news.api.base-url` | `http://localhost:8082` | Mock service base URL |
| `news.api.path` | `/api/v1/article/getArticles` | Article fetch path (`POST`) |
| `server.port` | `8090` | Agent HTTP port |
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

- `articleCount`, `articles[]` with `uri`, `title`, `url`, `date`, `dateTime`, `categories[]`
- Each category: `categoryId`, `categoryLabel`, `categoryDescription`, `score`, `matchedSignals`

Classification themes include geopolitical risk, trade/tariffs, disasters, logistics disruption, raw materials, cyber, and corporate restructuring. Rules live in `ArticleClassifier`.

### Build

```bash
cd agents/news-agent && mvn -q compile
```

---

## locations-agent

**Location name → coordinates** against the mock catalog (`GET /api/v1/places`). Matching order: **exact** normalized name, **substring** containment, **token overlap** (Jaccard), **Levenshtein**-style fuzzy score. Responses include `matchKind` (`EXACT`, `CONTAINS`, `TOKEN_OVERLAP`, `FUZZY`) and `confidence` (0–1).

### Run

```bash
cd agents/locations-agent && mvn spring-boot:run
```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `locations.api.base-url` | `http://localhost:8082` | Mock service base URL |
| `locations.api.catalog-path` | `/api/v1/places` | Catalog listing (`GET`) |
| `server.port` | `8091` | Agent HTTP port |
| `locations.resolve.min-confidence` | `0.55` | Minimum score to accept a non-exact match |

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/resolve-location?name=...` | Single lookup; **404** if no match |
| `POST` | `/api/agent/resolve-locations` | Body: `{ "queries": ["…"] }` — each entry may have `resolved: null` |
| `POST` | `/api/agent/refresh-catalog` | Reload catalog from the mock service |

Examples:

```bash
# Exact match (illustrative)
curl -s "http://localhost:8091/api/agent/resolve-location?name=Dubai%20City"

# Batch: mix of hits and misses
curl -s "http://localhost:8091/api/agent/resolve-locations" \
  -H "Content-Type: application/json" \
  -d '{"queries":["Strait of Hormuz","Panama","NowhereLandXYZ123"]}'
```

Sample **single** response shape:

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

## Project layout

```
agents/
├── readme.md
├── news-agent/
│   ├── pom.xml
│   └── src/main/java/com/hackathon/newsagent/...
└── locations-agent/
    ├── pom.xml
    └── src/main/java/com/hackathon/locationsagent/...
```
