# Agents

This folder holds Spring Boot **agents** that call shared mock APIs in the repo and add reasoning or enrichment on top.

## news-agent

Supply-chain **news classification** service. It pulls articles from the mock news API (`mockServices`), runs a weighted lexicon classifier over each headline and body, and returns **multi-label** risk categories with scores and matched keyword signals.

### Prerequisites

- **Java 21**
- **Maven** (or use `./mvnw` from `mockServices` if configured)
- The **mock news API** must be reachable (see [Run order](#run-order))

### Run order

From the **repository root** (`CursorHackathon/`):

1. Start the mock API (default port **8082** — see `mockServices/src/main/resources/application.properties`):

   ```bash
   cd mockServices && mvn spring-boot:run
   ```

2. In another terminal, start the agent (default port **8090**):

   ```bash
   cd agents/news-agent && mvn spring-boot:run
   ```

### Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `news.api.base-url` | `http://localhost:8082` | Base URL of the mock news service |
| `news.api.path` | `/api/v1/article/getArticles` | Path for `POST` article fetch |
| `server.port` | `8090` | Port for this agent |
| `agent.classification.min-score` | `0.12` | Minimum category score (0–1) to include a label |
| `agent.classification.max-categories-per-article` | `4` | Max categories returned per article |

Override via `application.properties`, environment variables, or `SPRING_APPLICATION_JSON` as usual for Spring Boot.

### HTTP API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/agent/classified-news` | Fetch articles from the news API and return classifications |

Example:

```bash
curl -s "http://localhost:8090/api/agent/classified-news" | python3 -m json.tool
```

If the news API is down or unreachable, the agent responds with **503** and a JSON body `{ "message": "..." }`.

### Response shape

- `articleCount` — number of articles returned by the news API
- `articles[]` — each item includes `uri`, `title`, `url`, `date`, `dateTime`, and `categories[]`
- Each category has:

  - `categoryId` — enum name (e.g. `INFRASTRUCTURE_LOGISTICS_DISRUPTIONS`)
  - `categoryLabel` — human-readable title
  - `categoryDescription` — short analyst-style description
  - `score` — relative weight (0–1) among categories that matched for that article
  - `matchedSignals` — keyword phrases that fired

### Classification themes

The agent maps text to **supply-chain risk** buckets such as:

- Geopolitical unrest and security (shipping lanes, sanctions, maritime risk)
- Trade policy and tariffs
- Environmental and natural disasters
- Infrastructure and logistics disruptions
- Raw material and resource scarcity
- Technology and cybersecurity
- Corporate restructuring

Rules live in `ArticleClassifier` (weighted terms). Tune or replace with an LLM or embedding model later without changing the REST contract.

### Build

```bash
cd agents/news-agent
mvn -q compile
mvn -q test
```

### Project layout

```
agents/
├── readme.md          # this file
└── news-agent/        # Spring Boot app (com.hackathon.newsagent)
    ├── pom.xml
    └── src/main/java/...
```
