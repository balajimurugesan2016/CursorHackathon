import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { parseCoord, projectToSvg } from '../util/geo';

const viewBox = { width: 600, height: 400 };

export function SupplyNetworkMap() {
  const { data: plants, loading, error } = useAsync(() => enterpriseApi.listPlants(), []);
  const { data: suppliers } = useAsync(() => enterpriseApi.listSuppliers(), []);

  const plantPts =
    plants?.flatMap((p) => {
      const lat = parseCoord(p.latitude);
      const lon = parseCoord(p.longitude);
      if (lat == null || lon == null) return [];
      return [{ name: p.plantName, ...projectToSvg(lat, lon, viewBox.width, viewBox.height) }];
    }) ?? [];

  const supplierPts =
    suppliers?.flatMap((s) => {
      const lat = parseCoord(s.latitude);
      const lon = parseCoord(s.longitude);
      if (lat == null || lon == null) return [];
      return [projectToSvg(lat, lon, viewBox.width, viewBox.height)];
    }) ?? [];

  const routes: { x1: number; y1: number; x2: number; y2: number }[] = [];
  for (const pp of plantPts) {
    for (const sp of supplierPts) {
      routes.push({ x1: pp.x, y1: pp.y, x2: sp.x, y2: sp.y });
    }
  }
  const maxRoutes = 12;
  const routeSlice = routes.slice(0, maxRoutes);

  return (
    <div
      style={{
        flex: 1,
        minWidth: 0,
        background: 'var(--bg-card)',
        borderRadius: 4,
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <div
        style={{
          padding: '12px 16px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          borderBottom: '1px solid var(--border)',
        }}
      >
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 11,
            fontWeight: 600,
            letterSpacing: 2,
            color: 'var(--text-tertiary)',
          }}
        >
          GLOBAL SUPPLY NETWORK
        </span>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 6,
                height: 6,
                borderRadius: '50%',
                background: 'var(--accent)',
              }}
            />
            <span
              style={{
                fontFamily: 'var(--font-mono)',
                fontSize: 10,
                fontWeight: 500,
                color: 'var(--text-secondary)',
              }}
            >
              Plants
            </span>
          </div>
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 6,
                height: 6,
                borderRadius: '50%',
                background: 'var(--warning)',
              }}
            />
            <span
              style={{
                fontFamily: 'var(--font-mono)',
                fontSize: 10,
                fontWeight: 500,
                color: 'var(--text-secondary)',
              }}
            >
              Suppliers
            </span>
          </div>
        </div>
      </div>

      <div
        style={{
          flex: 1,
          background: 'var(--bg-surface)',
          position: 'relative',
          minHeight: 300,
        }}
      >
        {error && (
          <div
            style={{
              padding: 16,
              color: 'var(--error)',
              fontFamily: 'var(--font-mono)',
              fontSize: 12,
            }}
          >
            {error}
          </div>
        )}
        {loading && !plantPts.length && !error && (
          <div style={{ padding: 16, color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
            Loading network…
          </div>
        )}
        <svg
          viewBox={`0 0 ${viewBox.width} ${viewBox.height}`}
          preserveAspectRatio="xMidYMid meet"
          style={{
            width: '100%',
            height: '100%',
            minHeight: 300,
          }}
        >
          <line x1={0} y1={100} x2={600} y2={100} stroke="#1E293B40" strokeWidth={1} />
          <line x1={0} y1={200} x2={600} y2={200} stroke="#1E293B40" strokeWidth={1} />
          <line x1={0} y1={300} x2={600} y2={300} stroke="#1E293B40" strokeWidth={1} />
          <line x1={150} y1={0} x2={150} y2={400} stroke="#1E293B40" strokeWidth={1} />
          <line x1={300} y1={0} x2={300} y2={400} stroke="#1E293B40" strokeWidth={1} />
          <line x1={450} y1={0} x2={450} y2={400} stroke="#1E293B40" strokeWidth={1} />

          {routeSlice.map((r, i) => (
            <line
              key={i}
              x1={r.x1}
              y1={r.y1}
              x2={r.x2}
              y2={r.y2}
              stroke="#22D3EE30"
              strokeWidth={1}
            />
          ))}

          {supplierPts.map((s, i) => (
            <circle key={`s-${i}`} cx={s.x} cy={s.y} r={2.5} fill="var(--warning)" opacity={0.75} />
          ))}

          {plantPts.map((p) => (
            <g key={p.name}>
              <circle cx={p.x} cy={p.y} r={5} fill="var(--accent)" />
              <text
                x={p.x}
                y={p.y - 14}
                textAnchor="middle"
                fill="var(--accent)"
                fontFamily="var(--font-mono)"
                fontSize={9}
                fontWeight={500}
              >
                {p.name}
              </text>
            </g>
          ))}
        </svg>

        <div
          style={{
            position: 'absolute',
            top: 16,
            left: 16,
            display: 'flex',
            gap: 16,
            alignItems: 'center',
          }}
        >
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 600,
              letterSpacing: 1,
              color: 'var(--accent)',
            }}
          >
            {plants?.length ?? 0} PLANTS (ENTERPRISE)
          </span>
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 500,
              letterSpacing: 1,
              color: 'var(--text-muted)',
            }}
          >
            {plantPts.length} with lat/lon on map
          </span>
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 500,
              letterSpacing: 1,
              color: 'var(--text-muted)',
            }}
          >
            {supplierPts.length} SUPPLIERS (geo)
          </span>
        </div>
      </div>
    </div>
  );
}
