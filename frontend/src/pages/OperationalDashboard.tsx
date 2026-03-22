import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SupplyNetworkMap } from '../components/SupplyNetworkMap';
import { DisruptionsPanel } from '../components/DisruptionsPanel';

export function OperationalDashboard() {
  const { data: staticData, loading: loadingStatic, error: errStatic } = useAsync(async () => {
    const [plants, suppliers] = await Promise.all([
      enterpriseApi.listPlants(),
      enterpriseApi.listSuppliers(),
    ]);
    return { plants, suppliers };
  }, []);

  const plantCount = staticData?.plants.length ?? 0;
  const supplierCount = staticData?.suppliers.length ?? 0;

  return (
    <Layout>
      <HeaderBar
        title="OPERATIONAL DASHBOARD"
        showLive
        searchPlaceholder="Search plants, suppliers..."
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
        {errStatic && (
          <div
            style={{
              padding: 12,
              background: 'var(--error-dim)',
              color: 'var(--error)',
              fontFamily: 'var(--font-mono)',
              fontSize: 12,
              borderRadius: 4,
            }}
          >
            {errStatic} — start enterpriseservice (8085).
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
            value={loadingStatic ? '…' : plantCount}
            valueColor="primary"
            subtext="GET /api/v1/plants"
            subtextColor="primary"
          />
          <KPICard
            label="SUPPLIERS"
            value={loadingStatic ? '…' : supplierCount}
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
          <SupplyNetworkMap />
          <DisruptionsPanel report={null} loading={false} error={null} />
        </div>
      </div>
    </Layout>
  );
}
