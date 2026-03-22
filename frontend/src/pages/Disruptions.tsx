import { Filter, FileDown } from 'lucide-react';
import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SeverityBreakdown } from '../components/SeverityBreakdown';
import { RecentActivity } from '../components/RecentActivity';

export function Disruptions() {
  const { data: enterprisePlants, loading: loadingPlants, error: plantsError } = useAsync(
    () => enterpriseApi.listPlants(),
    []
  );

  const enterprisePlantTotal = enterprisePlants?.length ?? 0;

  return (
    <Layout>
      <HeaderBar
        title="DISRUPTIONS"
        subtitle="Enterprise plants and suppliers"
        showLive={false}
        rightContent={
          <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <button
              type="button"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 8,
                padding: '8px 16px',
                background: 'var(--bg-card)',
                border: '1px solid var(--border)',
                borderRadius: 6,
                color: 'var(--text-secondary)',
                fontFamily: 'var(--font-sans)',
                fontSize: 13,
                fontWeight: 500,
                cursor: 'pointer',
              }}
            >
              <Filter size={16} />
              Filter
            </button>
            <button
              type="button"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 8,
                padding: '8px 16px',
                background: 'var(--accent)',
                border: 'none',
                borderRadius: 6,
                color: 'var(--bg-page)',
                fontFamily: 'var(--font-sans)',
                fontSize: 13,
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              <FileDown size={16} />
              Export Report
            </button>
          </div>
        }
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
        {plantsError && (
          <div style={{ padding: 12, color: 'var(--error)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
            {plantsError}
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
            value={loadingPlants ? '…' : enterprisePlantTotal}
            valueColor="primary"
            subtext="GET /api/v1/plants"
            subtextColor="primary"
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
                PLANT LIST
              </span>
              <div
                style={{
                  background: 'var(--accent-dim)',
                  borderRadius: 4,
                  padding: '2px 8px',
                }}
              >
                <span
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 11,
                    fontWeight: 700,
                    color: 'var(--accent)',
                  }}
                >
                  {loadingPlants ? '…' : enterprisePlants?.length ?? 0} plants
                </span>
              </div>
            </div>
            <div style={{ flex: 1, overflow: 'auto' }}>
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
                  </tr>
                </thead>
                <tbody>
                  {(enterprisePlants ?? []).map((p) => (
                    <tr key={p.id} style={{ borderBottom: '1px solid var(--border)' }}>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-primary)',
                          fontWeight: 600,
                        }}
                      >
                        {p.plantName}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 11,
                          color: 'var(--text-secondary)',
                        }}
                      >
                        {p.location ?? '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div
            style={{
              width: 320,
              minWidth: 320,
              display: 'flex',
              flexDirection: 'column',
              gap: 16,
            }}
          >
            <SeverityBreakdown critical={0} high={0} medium={0} />
            <RecentActivity items={[]} />
          </div>
        </div>
      </div>
    </Layout>
  );
}
