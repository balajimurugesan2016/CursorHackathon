import type { ReactNode } from 'react';
import { Search } from 'lucide-react';

interface HeaderBarProps {
  title?: string;
  subtitle?: string;
  showLive?: boolean;
  searchPlaceholder?: string;
  rightContent?: ReactNode;
}

export function HeaderBar({
  title = 'OPERATIONAL DASHBOARD',
  subtitle,
  showLive = true,
  searchPlaceholder = 'Search plants, suppliers...',
  rightContent,
}: HeaderBarProps) {
  return (
    <header
      style={{
        minHeight: 56,
        background: 'var(--bg-surface)',
        borderBottom: '1px solid var(--border)',
        padding: '12px 24px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <span
            style={{
              fontFamily: 'var(--font-sans)',
              fontSize: 18,
              fontWeight: 600,
              letterSpacing: 2,
              color: 'var(--text-primary)',
            }}
          >
            {title}
          </span>
          {showLive && (
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 6,
                padding: '4px 10px',
                background: 'var(--bg-card)',
                borderRadius: 4,
              }}
            >
              <span
                style={{
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  background: 'var(--success)',
                }}
              />
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 11,
                  fontWeight: 700,
                  letterSpacing: 1.5,
                  color: 'var(--success)',
                }}
              >
                LIVE
              </span>
            </div>
          )}
        </div>
        {subtitle && (
          <span
            style={{
              fontFamily: 'var(--font-sans)',
              fontSize: 12,
              fontWeight: 400,
              color: 'var(--text-muted)',
            }}
          >
            {subtitle}
          </span>
        )}
      </div>

      {rightContent ?? (
        <div
          style={{
            width: 240,
            display: 'flex',
            alignItems: 'center',
            gap: 8,
            padding: '8px 12px',
            background: 'var(--bg-card)',
            borderRadius: 6,
          }}
        >
          <Search size={16} color="var(--text-muted)" strokeWidth={2} />
          <span
            style={{
              fontFamily: 'var(--font-sans)',
              fontSize: 13,
              fontWeight: 400,
              color: 'var(--text-muted)',
            }}
          >
            {searchPlaceholder}
          </span>
        </div>
      )}
    </header>
  );
}
