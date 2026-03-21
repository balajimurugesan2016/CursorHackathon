import { CircleAlert, TriangleAlert, Info } from 'lucide-react';
import { useAsync } from '../hooks/useAsync';
import { simulationApi } from '../api/client';
import type { PlantSupplyRiskDto } from '../api/types';

function severityIcon(score: number) {
  if (score >= 0.5) return { Icon: CircleAlert, sev: 'P1' as const, border: 'var(--error)', bg: '#EF444408', ic: 'var(--error)' };
  if (score >= 0.2) return { Icon: TriangleAlert, sev: 'P2' as const, border: 'var(--warning)', bg: '#FACC1508', ic: 'var(--warning)' };
  return { Icon: Info, sev: 'P3' as const, border: 'var(--info)', bg: '#3B82F608', ic: 'var(--info)' };
}

function topRisks(plants: PlantSupplyRiskDto[]): PlantSupplyRiskDto[] {
  return [...plants]
    .sort((a, b) => b.plantRiskScore - a.plantRiskScore)
    .filter((p) => p.plantRiskScore > 0 || p.disturbanceCertainty > 0)
    .slice(0, 5);
}

export function DisruptionsPanel() {
  const { data: report, loading, error } = useAsync(() => simulationApi.supplyChainRiskReport(), []);

  const rows = report ? topRisks(report.plants ?? []) : [];
  const badgeCount = rows.length;

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
          TOP RISK SIGNALS
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
            {loading ? '…' : badgeCount}
          </span>
        </div>
      </div>

      {error && (
        <div style={{ padding: 16, color: 'var(--error)', fontFamily: 'var(--font-mono)', fontSize: 11 }}>{error}</div>
      )}

      <div
        style={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          gap: 0,
          overflow: 'auto',
        }}
      >
        {loading && !rows.length && !error && (
          <div style={{ padding: 16, color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
            Loading risk report…
          </div>
        )}
        {!loading && !error && rows.length === 0 && (
          <div style={{ padding: 16, color: 'var(--text-secondary)', fontFamily: 'var(--font-sans)', fontSize: 13 }}>
            No elevated plant risk in the current report. Ensure enterprise plants and the agent stack are running.
          </div>
        )}
        {rows.map((p, i) => {
          const { Icon, sev, border, bg, ic } = severityIcon(Math.max(p.plantRiskScore, p.disturbanceCertainty));
          const title = p.plantName;
          const desc =
            p.rationale?.slice(0, 120) ||
            `Risk ${(p.plantRiskScore * 100).toFixed(1)}% · Disturbance ${(p.disturbanceCertainty * 100).toFixed(1)}%`;
          return (
            <div
              key={p.plantId ?? p.plantName}
              style={{
                display: 'flex',
                gap: 12,
                padding: '14px 16px',
                background: bg,
                borderLeft: `3px solid ${border}`,
                borderBottom: i < rows.length - 1 ? '1px solid var(--bg-surface)' : 'none',
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
                <Icon size={18} color={ic} strokeWidth={2} />
                <span
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 9,
                    fontWeight: 700,
                    color: ic,
                  }}
                >
                  {sev}
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
                  {title}
                </span>
                <span
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    fontWeight: 500,
                    color: 'var(--text-secondary)',
                  }}
                >
                  {desc}
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
