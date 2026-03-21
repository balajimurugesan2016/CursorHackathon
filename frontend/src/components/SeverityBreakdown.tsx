interface SeverityBreakdownProps {
  critical: number;
  high: number;
  medium: number;
}

export function SeverityBreakdown({ critical, high, medium }: Readonly<SeverityBreakdownProps>) {
  const total = critical + high + medium || 1;
  const items = [
    { label: 'Critical', color: 'var(--error)', count: critical, width: (critical / total) * 100 },
    { label: 'High', color: 'var(--warning)', count: high, width: (high / total) * 100 },
    { label: 'Medium', color: 'var(--success)', count: medium, width: (medium / total) * 100 },
  ];

  return (
    <div
      style={{
        background: 'var(--bg-card)',
        borderRadius: 4,
        padding: 16,
        display: 'flex',
        flexDirection: 'column',
        gap: 12,
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
        RISK BUCKETS (PLANTS)
      </span>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {items.map((item) => (
          <div key={item.label} style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span
                  style={{
                    width: 8,
                    height: 8,
                    borderRadius: '50%',
                    background: item.color,
                  }}
                />
                <span
                  style={{
                    fontFamily: 'var(--font-sans)',
                    fontSize: 12,
                    fontWeight: 500,
                    color: 'var(--text-primary)',
                  }}
                >
                  {item.label}
                </span>
              </div>
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 11,
                  fontWeight: 600,
                  color: 'var(--text-secondary)',
                }}
              >
                {item.count}
              </span>
            </div>
            <div
              style={{
                height: 6,
                background: 'var(--bg-surface)',
                borderRadius: 3,
                overflow: 'hidden',
              }}
            >
              <div
                style={{
                  width: `${item.width}%`,
                  height: '100%',
                  background: item.color,
                  borderRadius: 3,
                }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
