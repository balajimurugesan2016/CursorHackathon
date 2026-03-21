import { useCallback, useEffect, useRef, useState } from "react";
import { fetchReasoningReport } from "./api";
import type { ArticleReasoningDto, ReasoningReportResponse } from "./types";
import "./App.css";

/** Keep in sync with reasoning.pipeline defaults in application.properties */
const RADIUS_MIN = 1;
const RADIUS_MAX = 500;
const RADIUS_DEFAULT = 100;

function pct(n: number): string {
  return `${Math.round(n * 1000) / 10}%`;
}

function ArticleCard({ row }: { row: ArticleReasoningDto }) {
  const a = row.classified;
  const impact = a.shippingRouteImpact;
  const prob = impact?.probability ?? 0;

  return (
    <article className="card">
      <div className="card-head">
        <h2 className="card-title">{a.title || "(no title)"}</h2>
        <div className="card-meta">
          {a.dateTime || a.date || "—"}
          {a.url ? (
            <>
              {" · "}
              <a href={a.url} target="_blank" rel="noreferrer">
                Source
              </a>
            </>
          ) : null}
        </div>
      </div>
      <div className="card-body">
        {a.body ? (
          <div>
            <div className="section-label">Summary / body</div>
            <p className="body-preview">{a.body}</p>
          </div>
        ) : null}

        <div>
          <div className="section-label">News categories</div>
          {a.categories?.length ? (
            <div className="chips">
              {a.categories.map((c) => (
                <span key={c.categoryId} className="chip" title={c.categoryDescription}>
                  {c.categoryLabel}
                  <span className="chip-score">{pct(c.score)}</span>
                </span>
              ))}
            </div>
          ) : (
            <p className="empty-note">No categories above threshold.</p>
          )}
        </div>

        <div className="route-impact">
          <div className="section-label">Shipping route impact (estimated)</div>
          <div className="meter" title={`Probability ${prob}`}>
            <div className="meter-fill" style={{ width: pct(prob) }} />
          </div>
          <div className="route-meta">
            <span>
              <strong>{pct(prob)}</strong> likelihood
            </span>
            {impact?.matchedSignals?.length ? (
              <span>{impact.matchedSignals.length} lexicon matches</span>
            ) : null}
          </div>
          {impact?.matchedSignals?.length ? (
            <div className="pill-list">
              {impact.matchedSignals.map((s) => (
                <span key={s} className="pill">
                  {s}
                </span>
              ))}
            </div>
          ) : null}
        </div>

        <div>
          <div className="section-label">Place catalog mentions</div>
          {row.catalogMentions?.length ? (
            <div className="pill-list">
              {row.catalogMentions.map((m) => (
                <span key={m} className="pill">
                  {m}
                </span>
              ))}
            </div>
          ) : (
            <p className="empty-note">No catalog place names detected in text.</p>
          )}
        </div>

        <div>
          <div className="section-label">Resolved locations</div>
          {row.resolvedLocations?.length ? (
            <div className="loc-table-wrap">
              <table className="loc-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Lat</th>
                    <th>Lon</th>
                    <th>Confidence</th>
                  </tr>
                </thead>
                <tbody>
                  {row.resolvedLocations.map((loc) => (
                    <tr key={`${loc.matchedName}-${loc.latitude}-${loc.longitude}`}>
                      <td>{loc.matchedName}</td>
                      <td>{loc.placeType}</td>
                      <td>{loc.latitude.toFixed(4)}</td>
                      <td>{loc.longitude.toFixed(4)}</td>
                      <td>{pct(loc.confidence)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p className="empty-note">None resolved.</p>
          )}
        </div>

        <div>
          <div className="section-label">Vessels near mentioned places</div>
          {row.vesselsNearLocations?.length ? (
            row.vesselsNearLocations.map((block) => (
              <div key={`${block.anchorMatchedName}-${block.latitude}`} className="vessel-block">
                <div className="vessel-block-title">{block.anchorMatchedName}</div>
                <div className="vessel-block-sub">
                  {block.vesselCount} vessel(s) within {block.radiusKm} km ·{" "}
                  {block.latitude.toFixed(4)}, {block.longitude.toFixed(4)}
                </div>
                {block.vessels?.length ? (
                  <div className="vessel-grid">
                    {block.vessels.map((v) => (
                      <div key={v.mmsi || v.name} className="vessel-mini">
                        <strong>{v.name || v.mmsi || "Vessel"}</strong>
                        MMSI {v.mmsi || "—"} · {v.speed ?? "?"} kn · course {v.course ?? "—"}°
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="empty-note">No vessel records in response.</p>
                )}
              </div>
            ))
          ) : (
            <p className="empty-note">No vessel searches (no resolved locations or empty).</p>
          )}
        </div>
      </div>
    </article>
  );
}

export default function App() {
  const [data, setData] = useState<ReasoningReportResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [radiusKm, setRadiusKm] = useState(RADIUS_DEFAULT);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const r = await fetchReasoningReport(radiusKm);
      setData(r);
    } catch (e) {
      setData(null);
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }, [radiusKm]);

  const didInitialFetch = useRef(false);
  useEffect(() => {
    if (didInitialFetch.current) {
      return;
    }
    didInitialFetch.current = true;
    void load();
  }, [load]);

  return (
    <div className="app-root">
      <div className="app">
      <header className="header">
        <div>
          <div className="hackathon-badge">Live pipeline</div>
          <h1 className="neon-title">Reasoning agent report</h1>
          <p className="subtitle">
            Live view of <code>/api/agent/reasoning-report?radiusKm=…</code> — classified news, place
            mentions, resolved coordinates, and nearby vessels (radius flows to vessel-agent).
          </p>
          <div className="radius-panel" aria-label="Vessel search radius">
            <label className="radius-label" htmlFor="radius-range">
              Vessel search radius
            </label>
            <div className="radius-row">
              <input
                id="radius-range"
                className="radius-slider"
                type="range"
                min={RADIUS_MIN}
                max={RADIUS_MAX}
                step={1}
                value={radiusKm}
                onChange={(e) => setRadiusKm(Number(e.target.value))}
              />
              <input
                className="radius-input"
                type="number"
                min={RADIUS_MIN}
                max={RADIUS_MAX}
                step={1}
                value={radiusKm}
                onChange={(e) => {
                  const v = Number(e.target.value);
                  if (!Number.isFinite(v)) return;
                  setRadiusKm(Math.min(RADIUS_MAX, Math.max(RADIUS_MIN, Math.round(v))));
                }}
                aria-label="Radius in kilometers"
              />
              <span className="radius-unit">km</span>
            </div>
            <p className="radius-hint">
              Allowed: {RADIUS_MIN}–{RADIUS_MAX} km (see reasoning.pipeline on server).
            </p>
          </div>
        </div>
        <div className="actions">
          <button type="button" className="btn btn-primary" onClick={() => void load()} disabled={loading}>
            {loading ? "Loading…" : "Refresh"}
          </button>
        </div>
      </header>

      {error ? (
        <div className="banner banner-error" role="alert">
          <strong>Could not load report.</strong> {error}
          <br />
          <span className="meta">
            Ensure mock services and all agents are running (reasoning-agent on port 8093). For{" "}
            <code>vite preview</code>, set <code>VITE_API_BASE=http://localhost:8093</code> or enable CORS.
          </span>
        </div>
      ) : null}

      {loading && !data ? (
        <div className="loading">Fetching reasoning report…</div>
      ) : null}

      {data ? (
        <>
          <p className="summary">
            <strong>{data.articleCount}</strong> article(s) · vessel search radius{" "}
            <strong>{data.searchRadiusKm}</strong> km
          </p>
          <div className="articles">
            {data.articles.map((row, index) => (
              <ArticleCard
                key={row.classified.uri || `${index}-${row.classified.title}`}
                row={row}
              />
            ))}
          </div>
        </>
      ) : null}

      {!loading && !error && !data ? (
        <p className="empty-note">No data returned.</p>
      ) : null}
      </div>
    </div>
  );
}
