import { FlaskConical } from 'lucide-react';

type DataMode = 'real' | 'simulation';

interface DataModeToggleProps {
  value: DataMode;
  onChange: (mode: DataMode) => void;
}

export function DataModeToggle({ value, onChange }: DataModeToggleProps) {
  return (
    <div
      style={{
        display: 'flex',
        background: 'var(--bg-card)',
        borderRadius: 6,
        overflow: 'hidden',
        border: '1px solid var(--border)',
      }}
    >
      <button
        type="button"
        onClick={() => onChange('real')}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 6,
          padding: '8px 16px',
          border: 'none',
          background: value === 'real' ? 'var(--success-dim)' : 'transparent',
          color: value === 'real' ? 'var(--success)' : 'var(--text-secondary)',
          cursor: 'pointer',
          fontFamily: 'var(--font-mono)',
          fontSize: 11,
          fontWeight: 600,
          letterSpacing: 1.5,
        }}
      >
        {value === 'real' && (
          <span
            style={{
              width: 6,
              height: 6,
              borderRadius: '50%',
              background: 'var(--success)',
            }}
          />
        )}
        REAL DATA
      </button>
      <button
        type="button"
        onClick={() => onChange('simulation')}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 6,
          padding: '8px 16px',
          border: 'none',
          background: value === 'simulation' ? 'var(--accent)' : 'transparent',
          color: value === 'simulation' ? 'var(--bg-page)' : 'var(--text-secondary)',
          cursor: 'pointer',
          fontFamily: 'var(--font-mono)',
          fontSize: 11,
          fontWeight: 600,
          letterSpacing: 1.5,
        }}
      >
        <FlaskConical size={14} />
        SIMULATION
      </button>
    </div>
  );
}
