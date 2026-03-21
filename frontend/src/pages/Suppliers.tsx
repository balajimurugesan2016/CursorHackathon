import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SupplierMap } from '../components/SupplierMap';

export function Suppliers() {
  const { data: suppliers, loading, error } = useAsync(() => enterpriseApi.listSuppliers(), []);

  const activeContracts = suppliers?.filter((s) => (s.contractStatus ?? '').toUpperCase() === 'ACTIVE').length ?? 0;
  const pendingRenewal = suppliers?.filter((s) => (s.contractStatus ?? '').toUpperCase() === 'PENDING_RENEWAL').length ?? 0;
  const other = (suppliers?.length ?? 0) - activeContracts - pendingRenewal;

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
            label="TOTAL SUPPLIERS (ENTERPRISE)"
            value={loading ? '…' : suppliers?.length ?? 0}
            valueColor="primary"
            subtext="GET /api/v1/suppliers"
            subtextColor="primary"
          />
          <KPICard
            label="ACTIVE CONTRACTS"
            value={loading ? '…' : activeContracts}
            valueColor="success"
          />
          <KPICard
            label="PENDING RENEWAL"
            value={loading ? '…' : pendingRenewal}
            valueColor="error"
          />
          <KPICard
            label="OTHER STATUS"
            value={loading ? '…' : other}
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
                {!loading && (suppliers ?? []).length === 0 && (
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
                      No suppliers from /api/v1/suppliers
                    </td>
                  </tr>
                )}
                {(suppliers ?? []).map((supplier) => {
                  const status = (supplier.contractStatus ?? '').toUpperCase();
                  const isActive = status === 'ACTIVE';
                  return (
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
                          {(supplier.contractStatus ?? '—').toUpperCase()}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

          <div style={{ width: 400, minWidth: 400 }}>
            <SupplierMap />
          </div>
        </div>
      </div>
    </Layout>
  );
}
