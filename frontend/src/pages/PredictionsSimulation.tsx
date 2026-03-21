import { useMemo, useState } from 'react';
import { useAsync } from '../hooks/useAsync';
import { enterpriseApi, simulationApi } from '../api/client';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { WorldMap } from '../components/WorldMap';
import { DataModeToggle } from '../components/DataModeToggle';
import { EventProbabilityCard } from '../components/EventProbabilityCard';

function badgeForRisk(r: number): { badge: string; badgeColor: string } {
  if (r >= 0.5) return { badge: 'CRITICAL', badgeColor: 'var(--error)' };
  if (r >= 0.2) return { badge: 'HIGH', badgeColor: 'var(--warning)' };
  if (r >= 0.05) return { badge: 'MEDIUM', badgeColor: '#F59E0B' };
  return { badge: 'LOW', badgeColor: 'var(--accent)' };
}

export function PredictionsSimulation() {
  const [mode, setMode] = useState<'real' | 'simulation'>('real');

  const { data, loading, error } = useAsync(async () => {
    const [risk, enterprisePlants] = await Promise.all([
      simulationApi.supplyChainRiskReport(),
      enterpriseApi.listPlants(),
    ]);
    return { risk, enterprisePlantTotal: enterprisePlants.length };
  }, []);

  const risk = data?.risk;
  const enterprisePlantTotal = data?.enterprisePlantTotal ?? 0;

  const cards = useMemo(() => {
    const plants = risk?.plants ?? [];
    return [...plants]
      .map((p) => ({
        p,
        score: Math.max(p.plantRiskScore, p.disturbanceCertainty),
      }))
      .sort((a, b) => b.score - a.score)
      .slice(0, 8)
      .map(({ p, score }, idx) => {
        const { badge, badgeColor } = badgeForRisk(score);
        const desc =
          p.rationale?.slice(0, 280) ||
          risk?.portfolioRationale?.slice(0, 280) ||
          '(no rationale)';
        return {
          key: `${p.plantName}-${idx}`,
          badge,
          badgeColor,
          percentage: Math.round(score * 100),
          title: p.plantName,
          description: desc,
        };
      });
  }, [risk]);

  return (
    <Layout>
      <HeaderBar
        title="PREDICTIONS & SIMULATION"
        subtitle="Simulation agent (supply-chain-risk) + enterpriseservice. No direct reasoning-agent calls from the UI."
        showLive={false}
        rightContent={<DataModeToggle value={mode} onChange={setMode} />}
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
              {mode === 'simulation' ? 'SIMULATION VIEW' : 'PLANT RISK SIGNALS'}
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
                SIMULATION AGENT
              </span>
            </div>
          </div>
          <div
            style={{
              display: 'flex',
              gap: 16,
              flexWrap: 'wrap',
            }}
          >
            {loading && (
              <span style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
                Loading simulation report…
              </span>
            )}
            {!loading &&
              cards.map((card) => (
                <EventProbabilityCard
                  key={card.key}
                  badge={card.badge}
                  badgeColor={card.badgeColor}
                  percentage={card.percentage}
                  title={card.title}
                  description={card.description}
                />
              ))}
            {!loading && cards.length === 0 && !error && (
              <span style={{ color: 'var(--text-secondary)' }}>
                No plant rows in the simulation report — check enterprise seeds and agent stack.
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
            label="ARTICLES (IN SIMULATION RUN)"
            value={loading ? '…' : risk?.reasoningArticleCount ?? 0}
            valueColor="primary"
          />
          <KPICard
            label="PORTFOLIO RISK"
            value={loading ? '…' : (risk?.portfolioRiskScore ?? 0).toFixed(3)}
            valueColor="primary"
          />
          <KPICard
            label="TOTAL PLANTS (ENTERPRISE)"
            value={loading ? '…' : enterprisePlantTotal}
            valueColor="primary"
            subtext={`simulation report rows: ${risk?.plantCount ?? 0}`}
            subtextColor="primary"
          />
          <KPICard
            label="VESSEL RADIUS (NM)"
            value={loading ? '…' : (risk?.searchRadiusNm ?? 0).toFixed(0)}
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
          <WorldMap title="PLANT MAP (ENTERPRISE)" />

          <div
            style={{
              width: 340,
              minWidth: 340,
              background: 'var(--bg-card)',
              borderRadius: 4,
              padding: 24,
              display: 'flex',
              flexDirection: 'column',
              gap: 16,
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
              DATA SOURCES
            </span>
            <p
              style={{
                fontFamily: 'var(--font-sans)',
                fontSize: 13,
                color: 'var(--text-secondary)',
                margin: 0,
                lineHeight: 1.5,
              }}
            >
              Cards and KPIs use the <strong>simulation agent</strong> supply-chain risk report (port 8094). Maps and
              registries use <strong>enterpriseservice</strong> (port 8085). Reasoning runs inside the simulation
              pipeline, not from the browser.
            </p>
          </div>
        </div>
      </div>
    </Layout>
  );
}
