const plants = [
  { name: 'Frankfurt', x: 197, y: 147 },
  { name: 'Shanghai', x: 447, y: 172 },
  { name: 'Houston', x: 117, y: 197 },
  { name: 'Mumbai', x: 367, y: 207 },
  { name: 'São Paulo', x: 177, y: 297 },
];

const supplierClusters = [
  { x: 210, y: 165, r: 2.5, opacity: 0.7 },
  { x: 225, y: 155, r: 2, opacity: 0.6 },
  { x: 390, y: 195, r: 2.5, opacity: 0.7 },
  { x: 470, y: 185, r: 2, opacity: 0.6 },
  { x: 135, y: 210, r: 2, opacity: 0.6 },
  { x: 245, y: 170, r: 1.5, opacity: 0.5 },
  { x: 410, y: 165, r: 2, opacity: 0.5 },
];

const routes = [
  { x1: 200, y1: 180, x2: 450, y2: 180 },
  { x1: 320, y1: 160, x2: 500, y2: 220 },
  { x1: 200, y1: 180, x2: 300, y2: 300 },
  { x1: 450, y1: 175, x2: 570, y2: 205 },
  { x1: 140, y1: 280, x2: 200, y2: 180 },
];

const viewBox = { x: 0, y: 0, width: 600, height: 400 };

export function SupplyNetworkMap() {
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
          3JS GLOBAL SUPPLY NETWORK
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
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 12,
                height: 1,
                background: 'var(--text-tertiary)',
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
              Routes
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
        <svg
          viewBox={`${viewBox.x} ${viewBox.y} ${viewBox.width} ${viewBox.height}`}
          preserveAspectRatio="xMidYMid meet"
          style={{
            width: '100%',
            height: '100%',
            minHeight: 300,
          }}
        >
          {/* Grid lines */}
          <line x1={0} y1={100} x2={600} y2={100} stroke="#1E293B40" strokeWidth={1} />
          <line x1={0} y1={200} x2={600} y2={200} stroke="#1E293B40" strokeWidth={1} />
          <line x1={0} y1={300} x2={600} y2={300} stroke="#1E293B40" strokeWidth={1} />
          <line x1={150} y1={0} x2={150} y2={400} stroke="#1E293B40" strokeWidth={1} />
          <line x1={300} y1={0} x2={300} y2={400} stroke="#1E293B40" strokeWidth={1} />
          <line x1={450} y1={0} x2={450} y2={400} stroke="#1E293B40" strokeWidth={1} />

          {/* Route lines */}
          {routes.map((r, i) => (
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

          {/* Supplier clusters */}
          {supplierClusters.map((s, i) => (
            <circle
              key={i}
              cx={s.x}
              cy={s.y}
              r={s.r}
              fill="var(--warning)"
              opacity={s.opacity}
            />
          ))}

          {/* Plant nodes */}
          {plants.map((p) => (
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
            5 NODES
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
            847 CONNECTIONS
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
            LATENCY: 42ms
          </span>
        </div>
      </div>
    </div>
  );
}
