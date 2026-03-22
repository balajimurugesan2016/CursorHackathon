import { useMemo } from 'react';
import { useAsync } from '../hooks/useAsync';
import { useProbabilityWebSocket } from '../hooks/useProbabilityWebSocket';
import { useMatchedSupplyChain } from '../hooks/useMatchedSupplyChain';
import { enterpriseApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { WorldMap } from '../components/WorldMap';
import { EventProbabilityCard } from '../components/EventProbabilityCard';
import type { ProbabilityItem } from '../api/types';

function badgeForProbability(pct: number): { badge: string; badgeColor: string } {
  if (pct > 70) return { badge: 'CRITICAL', badgeColor: 'var(--error)' };
  if (pct >= 40) return { badge: 'HIGH', badgeColor: 'var(--warning)' };
  if (pct >= 20) return { badge: 'MEDIUM', badgeColor: '#F59E0B' };
  return { badge: 'LOW', badgeColor: 'var(--accent)' };
}

export function PredictionsSimulation() {
  const { data: enterprisePlants, loading: loadingPlants, error: plantsError } = useAsync(
    () => enterpriseApi.listPlants(),
    []
  );
  const { data: suppliers, error: suppliersError } = useAsync(
    () => enterpriseApi.listSuppliers(),
    []
  );
  const { data: probability, loading: loadingProbability, error: probabilityError } = useProbabilityWebSocket();
  const { matchedSuppliers, plantsAtRisk } = useMatchedSupplyChain(
    probability,
    suppliers ?? null,
    enterprisePlants ?? null
  );
  const error = plantsError ?? suppliersError ?? probabilityError;
  const enterprisePlantTotal = enterprisePlants?.length ?? 0;

  const probabilityCards = useMemo(() => {
    const items: ProbabilityItem[] = probability?.items ?? [];
    return [...items]
      .filter((i) => i.probabilityPercent > 0)
      .sort((a, b) => b.probabilityPercent - a.probabilityPercent)
      .slice(0, 8)
      .map((item, idx) => {
        const { badge, badgeColor } = badgeForProbability(item.probabilityPercent);
        const locStr = item.locations?.length
          ? `Locations: ${item.locations.join(', ')}. `
          : '';
        const description = `${locStr}Event probability based on classified news and ship mobility data.`;
        return {
          key: `prob-${idx}-${item.title?.slice(0, 30) ?? ''}`,
          badge,
          badgeColor,
          percentage: item.probabilityPercent,
          title: item.title || 'Unknown event',
          description,
        };
      });
  }, [probability]);

  return (
    <Layout>
      <HeaderBar
        title="PREDICTIONS"
        subtitle="AI-powered event probability analysis from news and ship mobility data."
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
            flexDirection: 'column',
            gap: 16,
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
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
              EVENT PROBABILITY PREDICTIONS
            </span>
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 6,
                padding: '4px 8px',
                background: 'var(--accent-dim)',
                borderRadius: 4,
              }}
            >
              <span
                style={{
                  width: 4,
                  height: 4,
                  borderRadius: '50%',
                  background: 'var(--accent)',
                }}
              />
              <span
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: 10,
                  fontWeight: 600,
                  letterSpacing: 1,
                  color: 'var(--accent)',
                }}
              >
                AI PREDICTED
              </span>
            </div>
          </div>
          <div
            style={{
              display: 'flex',
              gap: 16,
              flexWrap: 'nowrap',
              overflowX: 'auto',
              overflowY: 'hidden',
              paddingBottom: 8,
            }}
          >
            {loadingProbability && (
              <span style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
                Loading event probabilities…
              </span>
            )}
            {!loadingProbability &&
              probabilityCards.map((card) => (
                <div key={card.key} style={{ flexShrink: 0, minWidth: 260 }}>
                  <EventProbabilityCard
                    badge={card.badge}
                    badgeColor={card.badgeColor}
                    percentage={card.percentage}
                    title={card.title}
                    description={card.description}
                  />
                </div>
              ))}
            {!loadingProbability && probabilityCards.length === 0 && !probabilityError && (
              <span style={{ color: 'var(--text-secondary)' }}>
                No event predictions — probability-service aggregates news-agent and ship-mobility.
              </span>
            )}
          </div>
        </div>

        <div
          style={{
            display: 'flex',
            gap: 16,
            flexWrap: 'wrap',
          }}
        >
          <KPICard
            label="EVENT PREDICTIONS"
            value={loadingProbability ? '…' : probability?.articleCount ?? 0}
            valueColor="primary"
            subtext="from probability-service"
            subtextColor="primary"
          />
          <KPICard
            label="TOTAL PLANTS (ENTERPRISE)"
            value={loadingPlants ? '…' : enterprisePlantTotal}
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
          <WorldMap
            title="RISK REGION"
            highlightedPlants={plantsAtRisk}
            matchedSuppliers={matchedSuppliers}
          />
        </div>
      </div>
    </Layout>
  );
}
