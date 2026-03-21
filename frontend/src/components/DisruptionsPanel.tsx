import { CircleAlert, TriangleAlert, Info } from 'lucide-react';

interface Disruption {
  severity: 'P1' | 'P2' | 'P3';
  title: string;
  description: string;
  time: string;
  icon: typeof CircleAlert;
  borderColor: string;
  bgColor: string;
  iconColor: string;
}

const disruptions: Disruption[] = [
  {
    severity: 'P1',
    title: 'Port of Shanghai — Congestion Delay',
    description: '48h avg delay · 12 shipments affected',
    time: '2m ago',
    icon: CircleAlert,
    borderColor: 'var(--error)',
    bgColor: '#EF444408',
    iconColor: 'var(--error)',
  },
  {
    severity: 'P2',
    title: 'European Rail Strike — Transit Impact',
    description: '3 routes disrupted · Est. 72h impact',
    time: '14m ago',
    icon: TriangleAlert,
    borderColor: 'var(--warning)',
    bgColor: '#FACC1508',
    iconColor: 'var(--warning)',
  },
  {
    severity: 'P3',
    title: 'Raw Material Shortage — Titanium Dioxide',
    description: '5 suppliers affected · Alt sourcing active',
    time: '1h ago',
    icon: Info,
    borderColor: 'var(--info)',
    bgColor: '#3B82F608',
    iconColor: 'var(--info)',
  },
];

export function DisruptionsPanel() {
  return (
    <div
      style={{
        width: 340,
        minWidth: 340,
        background: 'var(--bg-card)',
        borderRadius: 4,
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
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
          REAL-TIME DISRUPTIONS
        </span>
        <div
          style={{
            background: 'var(--error-dim)',
            borderRadius: 4,
            padding: '2px 8px',
          }}
        >
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 11,
              fontWeight: 700,
              color: 'var(--error)',
            }}
          >
            3
          </span>
        </div>
      </div>

      <div
        style={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          gap: 0,
          overflow: 'auto',
        }}
      >
        {disruptions.map((d, i) => (
          <div
            key={i}
            style={{
              display: 'flex',
              gap: 12,
              padding: '14px 16px',
              background: d.bgColor,
              borderLeft: `3px solid ${d.borderColor}`,
              borderBottom: i < disruptions.length - 1 ? '1px solid var(--bg-surface)' : 'none',
            }}
          >
            <div
              style={{
                width: 36,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 2,
              }}
            >
              <d.icon size={18} color={d.iconColor} strokeWidth={2} />
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 9,
                  fontWeight: 700,
                  color: d.iconColor,
                }}
              >
                {d.severity}
              </span>
            </div>
            <div
              style={{
                flex: 1,
                minWidth: 0,
                display: 'flex',
                flexDirection: 'column',
                gap: 4,
              }}
            >
              <span
                style={{
                  fontFamily: 'var(--font-sans)',
                  fontSize: 13,
                  fontWeight: 600,
                  color: 'var(--text-primary)',
                }}
              >
                {d.title}
              </span>
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 10,
                  fontWeight: 500,
                  color: 'var(--text-secondary)',
                }}
              >
                {d.description}
              </span>
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 9,
                  fontWeight: 500,
                  color: 'var(--text-muted)',
                }}
              >
                {d.time}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
