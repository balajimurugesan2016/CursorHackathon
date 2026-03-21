interface EventProbabilityCardProps {
  badge: string;
  badgeColor: string;
  percentage: number;
  title: string;
  description: string;
}

export function EventProbabilityCard({
  badge,
  badgeColor,
  percentage,
  title,
  description,
}: EventProbabilityCardProps) {
  return (
    <div
      style={{
        flex: 1,
        minWidth: 0,
        background: 'var(--bg-card)',
        borderRadius: 4,
        padding: 16,
        display: 'flex',
        flexDirection: 'column',
        gap: 8,
        border: '1px solid var(--border)',
      }}
    >
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
        }}
      >
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 9,
            fontWeight: 700,
            letterSpacing: 1.5,
            padding: '2px 6px',
            borderRadius: 4,
            background: badgeColor,
            color: '#0A0F1C',
          }}
        >
          {badge}
        </span>
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 24,
            fontWeight: 700,
            color: badgeColor,
          }}
        >
          {percentage}%
        </span>
      </div>
      <span
        style={{
          fontFamily: 'var(--font-sans)',
          fontSize: 14,
          fontWeight: 600,
          color: 'var(--text-primary)',
        }}
      >
        {title}
      </span>
      <span
        style={{
          fontFamily: 'var(--font-mono)',
          fontSize: 11,
          fontWeight: 500,
          color: 'var(--text-secondary)',
          lineHeight: 1.5,
        }}
      >
        {description}
      </span>
      <div
        style={{
          height: 3,
          background: 'var(--bg-surface)',
          borderRadius: 2,
          overflow: 'hidden',
          marginTop: 'auto',
        }}
      >
        <div
          style={{
            width: `${percentage}%`,
            height: '100%',
            background: badgeColor,
            borderRadius: 2,
          }}
        />
      </div>
    </div>
  );
}
