interface KPICardProps {
  label: string;
  value: string | number;
  valueColor?: 'primary' | 'success' | 'error';
}

export function KPICard({
  label,
  value,
  valueColor = 'primary',
}: KPICardProps) {
  const colorMap = {
    primary: 'var(--text-primary)',
    success: 'var(--success)',
    error: 'var(--error)',
  };

  return (
    <div
      style={{
        flex: 1,
        minWidth: 0,
        background: 'var(--bg-card)',
        borderRadius: 4,
        padding: '16px 20px',
        display: 'flex',
        flexDirection: 'column',
        gap: 8,
      }}
    >
      <span
        style={{
          fontFamily: 'var(--font-sans)',
          fontSize: 11,
          fontWeight: 600,
          letterSpacing: 2,
          color: 'var(--text-tertiary)',
        }}
      >
        {label}
      </span>
      <span
        style={{
          fontFamily: 'var(--font-mono)',
          fontSize: 32,
          fontWeight: 700,
          color: colorMap[valueColor],
        }}
      >
        {value}
      </span>
    </div>
  );
}
