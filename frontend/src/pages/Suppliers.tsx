import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';

export function Suppliers() {
  const { data: suppliers, loading, error } = useAsync(() => enterpriseApi.listSuppliers(), []);

  const ports = suppliers?.filter((s) => (s.location ?? '').toUpperCase().includes('PORT')).length ?? 0;
  const hubs = (suppliers?.length ?? 0) - ports;

  return (
    <Layout>
      <HeaderBar
        title="SUPPLIERS"
        showLive
        searchPlaceholder="Search suppliers..."
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
            label="TOTAL SUPPLIERS"
            value={loading ? '…' : suppliers?.length ?? 0}
            valueColor="primary"
          />
          <KPICard
            label="NAME CONTAINS PORT"
            value={loading ? '…' : ports}
            valueColor="primary"
          />
          <KPICard
            label="OTHER"
            value={loading ? '…' : hubs}
            valueColor="primary"
          />
          <KPICard
            label="CONTRACT STATUS (FILLED)"
            value={
              loading
                ? '…'
                : suppliers?.filter((s) => s.contractStatus != null && s.contractStatus !== '').length ?? 0
            }
            valueColor="primary"
          />
        </div>

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
              SUPPLIER REGISTRY
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
                    SUPPLIER
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
                    REGION HINT
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
                    CONTRACT
                  </th>
                </tr>
              </thead>
              <tbody>
                {(suppliers ?? []).map((supplier) => (
                  <tr
                    key={supplier.id}
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
                      {supplier.supplierName}
                    </td>
                    <td
                      style={{
                        padding: '12px 16px',
                        color: 'var(--text-secondary)',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 11,
                      }}
                    >
                      {supplier.location ?? '—'}
                    </td>
                    <td
                      style={{
                        padding: '12px 16px',
                        color: 'var(--text-secondary)',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 11,
                      }}
                    >
                      {supplier.latitude && supplier.longitude ? 'geo' : '—'}
                    </td>
                    <td
                      style={{
                        padding: '12px 16px',
                      }}
                    >
                      <span
                        style={{
                          padding: '2px 8px',
                          borderRadius: 4,
                          fontFamily: 'var(--font-mono)',
                          fontSize: 10,
                          fontWeight: 600,
                          background: 'var(--success-dim)',
                          color: 'var(--success)',
                        }}
                      >
                        {(supplier.contractStatus ?? '—').toUpperCase()}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </Layout>
  );
}
