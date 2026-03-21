import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { WorldMap } from '../components/WorldMap';

function formatCapacity(p: {
  capacityPct?: number | null;
  totalLines?: number | null;
  linesActive?: number | null;
}): string {
  if (p.capacityPct != null) return `${p.capacityPct}%`;
  if (p.totalLines != null) return `${p.linesActive ?? 0}/${p.totalLines} lines`;
  return '—';
}

export function Plants() {
  const { data: plants, loading, error } = useAsync(() => enterpriseApi.listPlants(), []);

  const active = plants?.filter((p) => (p.status ?? '').toLowerCase() === 'active').length ?? 0;
  const maintenance = (plants?.length ?? 0) - active;

  return (
    <Layout>
      <HeaderBar
        title="PLANTS"
        showLive
        searchPlaceholder="Search plants..."
      />
      <div
        style={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          gap: 20,
          padding: 24,
          overflow: 'auto',
        }}
      >
        {error && (
          <div style={{ padding: 12, color: 'var(--error)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
            {error}
          </div>
        )}
        <div
          style={{
            display: 'flex',
            gap: 16,
            flexWrap: 'wrap',
          }}
        >
          <KPICard
            label="TOTAL PLANTS (ENTERPRISE)"
            value={loading ? '…' : plants?.length ?? 0}
            valueColor="primary"
            subtext="GET /api/v1/plants"
            subtextColor="primary"
          />
          <KPICard
            label="ACTIVE"
            value={loading ? '…' : active}
            valueColor="success"
          />
          <KPICard
            label="OTHER STATUS"
            value={loading ? '…' : maintenance}
            valueColor="error"
          />
          <KPICard
            label="AVG CAPACITY %"
            value={
              loading
                ? '…'
                : (() => {
                    const withPct = plants?.filter((p) => p.capacityPct != null) ?? [];
                    if (!withPct.length) return '—';
                    const avg =
                      withPct.reduce((s, p) => s + (p.capacityPct ?? 0), 0) / withPct.length;
                    return `${avg.toFixed(1)}%`;
                  })()
            }
            valueColor="primary"
          />
        </div>

        <div
          style={{
            flex: 1,
            display: 'flex',
            gap: 16,
            minHeight: 0,
          }}
        >
          <div
            style={{
              flex: 1,
              background: 'var(--bg-card)',
              borderRadius: 4,
              overflow: 'hidden',
              display: 'flex',
              flexDirection: 'column',
              minWidth: 0,
            }}
          >
            <div
              style={{
                padding: '12px 16px',
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
                PLANT REGISTRY
              </span>
            </div>
            <div
              style={{
                flex: 1,
                overflow: 'auto',
              }}
            >
              <table
                style={{
                  width: '100%',
                  borderCollapse: 'collapse',
                  fontFamily: 'var(--font-sans)',
                  fontSize: 13,
                }}
              >
                <thead>
                  <tr>
                    <th
                      style={{
                        padding: '12px 16px',
                        textAlign: 'left',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 10,
                        fontWeight: 600,
                        letterSpacing: 1.5,
                        color: 'var(--text-tertiary)',
                        borderBottom: '1px solid var(--border)',
                      }}
                    >
                      PLANT
                    </th>
                    <th
                      style={{
                        padding: '12px 16px',
                        textAlign: 'left',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 10,
                        fontWeight: 600,
                        letterSpacing: 1.5,
                        color: 'var(--text-tertiary)',
                        borderBottom: '1px solid var(--border)',
                      }}
                    >
                      LOCATION
                    </th>
                    <th
                      style={{
                        padding: '12px 16px',
                        textAlign: 'left',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 10,
                        fontWeight: 600,
                        letterSpacing: 1.5,
                        color: 'var(--text-tertiary)',
                        borderBottom: '1px solid var(--border)',
                      }}
                    >
                      STATUS
                    </th>
                    <th
                      style={{
                        padding: '12px 16px',
                        textAlign: 'right',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 10,
                        fontWeight: 600,
                        letterSpacing: 1.5,
                        color: 'var(--text-tertiary)',
                        borderBottom: '1px solid var(--border)',
                      }}
                    >
                      CAPACITY
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {!loading && (plants ?? []).length === 0 && (
                    <tr>
                      <td
                        colSpan={4}
                        style={{
                          padding: 24,
                          textAlign: 'center',
                          color: 'var(--text-tertiary)',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 12,
                        }}
                      >
                        No plants from /api/v1/plants
                      </td>
                    </tr>
                  )}
                  {(plants ?? []).map((plant) => {
                    const st = (plant.status ?? 'active').toLowerCase();
                    const isActive = st === 'active';
                    return (
                      <tr
                        key={plant.id}
                        style={{
                          borderBottom: '1px solid var(--border)',
                        }}
                      >
                        <td
                          style={{
                            padding: '12px 16px',
                            color: 'var(--text-primary)',
                            fontWeight: 600,
                          }}
                        >
                          {plant.plantName}
                        </td>
                        <td
                          style={{
                            padding: '12px 16px',
                            color: 'var(--text-secondary)',
                            fontFamily: 'var(--font-mono)',
                            fontSize: 11,
                          }}
                        >
                          {plant.location ?? '—'}
                        </td>
                        <td style={{ padding: '12px 16px' }}>
                          <span
                            style={{
                              padding: '2px 8px',
                              borderRadius: 4,
                              fontFamily: 'var(--font-mono)',
                              fontSize: 10,
                              fontWeight: 600,
                              background: isActive ? 'var(--success-dim)' : 'var(--error-dim)',
                              color: isActive ? 'var(--success)' : 'var(--error)',
                            }}
                          >
                            {(plant.status ?? 'UNKNOWN').toUpperCase()}
                          </span>
                        </td>
                        <td
                          style={{
                            padding: '12px 16px',
                            textAlign: 'right',
                            color: 'var(--text-secondary)',
                            fontFamily: 'var(--font-mono)',
                            fontSize: 11,
                          }}
                        >
                          {formatCapacity(plant)}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          <div style={{ width: 400, minWidth: 400 }}>
            <WorldMap title="PLANT LOCATIONS" />
          </div>
        </div>
      </div>
    </Layout>
  );
}
