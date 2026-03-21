import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SupplyNetworkMap } from '../components/SupplyNetworkMap';
import { DisruptionsPanel } from '../components/DisruptionsPanel';

export function OperationalDashboard() {
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
        <div
          style={{
            display: 'flex',
            gap: 16,
            flexWrap: 'wrap',
          }}
        >
          <KPICard label="TOTAL PLANTS" value="12" valueColor="primary" />
          <KPICard label="ACTIVE SUPPLIERS" value="847" valueColor="primary" />
          <KPICard label="ACTIVE DISRUPTIONS" value="3" valueColor="error" />
          <KPICard label="ON-TIME DELIVERY" value="94.2%" valueColor="success" />
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
