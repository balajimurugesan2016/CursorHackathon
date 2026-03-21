interface ActivityItem {
  color: string;
  text: string;
  time?: string;
}

interface RecentActivityProps {
  items: ActivityItem[];
}

export function RecentActivity({ items }: Readonly<RecentActivityProps>) {
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
        ARTICLE SIGNALS (SIMULATION)
      </span>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>
        {items.length === 0 && (
          <span style={{ fontFamily: 'var(--font-sans)', fontSize: 13, color: 'var(--text-secondary)' }}>
            No articles in the current reasoning run.
          </span>
        )}
        {items.map((item, i) => (
          <div
            key={i}
            style={{
              display: 'flex',
              gap: 12,
              padding: '12px 0',
              borderBottom: i < items.length - 1 ? '1px solid var(--border)' : 'none',
            }}
          >
            <span
              style={{
                width: 8,
                height: 8,
                borderRadius: '50%',
                background: item.color,
                flexShrink: 0,
                marginTop: 6,
              }}
            />
            <div style={{ flex: 1, minWidth: 0 }}>
              <span
                style={{
                  fontFamily: 'var(--font-sans)',
                  fontSize: 13,
                  fontWeight: 500,
                  color: 'var(--text-primary)',
                }}
              >
                {item.text}
              </span>
              {item.time && (
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    fontWeight: 500,
                    color: 'var(--text-muted)',
                    marginTop: 2,
                  }}
                >
                  {item.time}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
