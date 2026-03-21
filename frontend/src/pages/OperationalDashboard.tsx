import { useAsync } from '../hooks/useAsync';
import { enterpriseApi, simulationApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SupplyNetworkMap } from '../components/SupplyNetworkMap';
import { DisruptionsPanel } from '../components/DisruptionsPanel';
import type { SupplyChainRiskReportResponse } from '../api/types';

function countExposed(report: SupplyChainRiskReportResponse | null | undefined): number {
  if (!report?.plants?.length) return 0;
  return report.plants.filter(
    (p) => p.plantRiskScore > 0.001 || p.disturbanceCertainty > 0.001
  ).length;
}

export function OperationalDashboard() {
  const { data, loading, error } = useAsync(async () => {
    const [plants, suppliers, risk] = await Promise.all([
      enterpriseApi.listPlants(),
      enterpriseApi.listSuppliers(),
      simulationApi.supplyChainRiskReport(),
    ]);
    return { plants, suppliers, risk };
  }, []);

  const plantCount = data?.plants.length ?? 0;
  const supplierCount = data?.suppliers.length ?? 0;
  const exposed = data?.risk ? countExposed(data.risk) : 0;
  const portfolioRisk = data?.risk?.portfolioRiskScore ?? 0;
  const healthPct = Math.max(0, Math.min(100, (1 - Math.min(portfolioRisk, 1)) * 100));

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
        {error && (
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
            {error} — start enterpriseservice (8085), simulation agent (supply-chain-risk, 8094), and the agent stack it depends on.
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
            value={loading ? '…' : plantCount}
            valueColor="primary"
            subtext="GET /api/v1/plants"
            subtextColor="primary"
          />
          <KPICard
            label="SUPPLIERS"
            value={loading ? '…' : supplierCount}
            valueColor="primary"
          />
          <KPICard
            label="SITES WITH ELEVATED SIGNAL"
            value={loading ? '…' : exposed}
            valueColor={exposed > 0 ? 'error' : 'success'}
          />
          <KPICard
            label="PORTFOLIO HEALTH (1 − risk)"
            value={loading ? '…' : `${healthPct.toFixed(1)}%`}
            valueColor="success"
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
          <DisruptionsPanel />
        </div>
      </div>
    </Layout>
  );
}
