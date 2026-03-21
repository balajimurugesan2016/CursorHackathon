import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import type { Shipment } from '../api/types';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';

function formatReceiveDate(s: string | null | undefined): string {
  if (!s) return '—';
  try {
    const d = new Date(s);
    return d.toISOString().slice(0, 10);
  } catch {
    return s;
  }
}

const statusDisplay: Record<string, string> = {
  IN_TRANSIT: 'In Transit',
  DELIVERED: 'Delivered',
  DELAYED: 'Delayed',
  PENDING: 'Pending',
  MAINTENANCE: 'Maintenance',
};

const statusColors: Record<string, string> = {
  IN_TRANSIT: 'var(--info)',
  DELIVERED: 'var(--success)',
  DELAYED: 'var(--error)',
  PENDING: 'var(--warning)',
  MAINTENANCE: 'var(--error)',
};

export function Shipments() {
  const { data: shipments, loading, error } = useAsync(() => enterpriseApi.listShipments(), []);

  const inTransit = shipments?.filter((s) => (s.status ?? '').toUpperCase() === 'IN_TRANSIT').length ?? 0;
  const delivered = shipments?.filter((s) => (s.status ?? '').toUpperCase() === 'DELIVERED').length ?? 0;
  const other = (shipments?.length ?? 0) - inTransit - delivered;
  const onTimeRate =
    shipments && shipments.length > 0 ? Math.round((delivered / shipments.length) * 100) : 0;

  return (
    <Layout>
      <HeaderBar
        title="SHIPMENTS"
        subtitle="Track shipments, deliveries, and logistics across your supply chain"
        showLive={false}
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
            label="TOTAL SHIPMENTS (ENTERPRISE)"
            value={loading ? '…' : shipments?.length ?? 0}
            valueColor="info"
            subtext="GET /api/v1/shipments"
            subtextColor="primary"
          />
          <KPICard
            label="IN TRANSIT"
            value={loading ? '…' : inTransit}
            valueColor="primary"
          />
          <KPICard
            label="DELIVERED"
            value={loading ? '…' : delivered}
            valueColor="success"
          />
          <KPICard
            label="OTHER STATUS"
            value={loading ? '…' : other}
            valueColor="warning"
          />
          <KPICard
            label="DELIVERED RATE"
            value={loading ? '…' : `${onTimeRate}%`}
            valueColor="success"
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
            minHeight: 0,
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
              SHIPMENT DIRECTORY
            </span>
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
                    ID
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
                    SHIPMENT ITEM
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
                    QUANTITY
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
                    SHIP NUMBER
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
                      textAlign: 'left',
                      fontFamily: 'var(--font-mono)',
                      fontSize: 10,
                      fontWeight: 600,
                      letterSpacing: 1.5,
                      color: 'var(--text-tertiary)',
                      borderBottom: '1px solid var(--border)',
                    }}
                  >
                    RECEIVE DATE
                  </th>
                </tr>
              </thead>
              <tbody>
                {!loading && (shipments ?? []).length === 0 && (
                  <tr>
                    <td
                      colSpan={9}
                      style={{
                        padding: 24,
                        textAlign: 'center',
                        color: 'var(--text-tertiary)',
                        fontFamily: 'var(--font-mono)',
                        fontSize: 12,
                      }}
                    >
                      No shipments from /api/v1/shipments
                    </td>
                  </tr>
                )}
                {(shipments ?? []).map((row: Shipment) => {
                  const status = (row.status ?? '').toUpperCase();
                  const statusColor = statusColors[status] ?? 'var(--text-secondary)';
                  const supplierName = row.suppliers?.[0]?.supplierName ?? '—';
                  const plantName = row.plants?.[0]?.plantName ?? row.plants?.[0]?.location ?? '—';
                  return (
                    <tr
                      key={row.id}
                      style={{ borderBottom: '1px solid var(--border)' }}
                    >
                      <td
                        style={{
                          padding: '12px 16px',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 12,
                          color: 'var(--accent)',
                          fontWeight: 600,
                        }}
                      >
                        {row.id}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-primary)',
                          fontWeight: 500,
                        }}
                      >
                        {row.shipmentItem ?? '—'}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-secondary)',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 11,
                        }}
                      >
                        {row.quantity == null ? '—' : String(row.quantity)}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-secondary)',
                        }}
                      >
                        {supplierName}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-secondary)',
                        }}
                      >
                        {plantName}
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-secondary)',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 11,
                        }}
                      >
                        {row.shipNumber ?? '—'}
                      </td>
                      <td style={{ padding: '12px 16px' }}>
                        <span
                          style={{
                            padding: '4px 10px',
                            borderRadius: 9999,
                            fontFamily: 'var(--font-mono)',
                            fontSize: 10,
                            fontWeight: 600,
                            background: `${statusColor}20`,
                            color: statusColor,
                          }}
                        >
                          {(statusDisplay[status] ?? status) || '—'}
                        </span>
                      </td>
                      <td
                        style={{
                          padding: '12px 16px',
                          color: 'var(--text-secondary)',
                          fontFamily: 'var(--font-mono)',
                          fontSize: 11,
                        }}
                      >
                        {formatReceiveDate(row.receiveDate ?? undefined)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </Layout>
  );
}
