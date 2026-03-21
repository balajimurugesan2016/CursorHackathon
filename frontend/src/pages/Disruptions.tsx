import { Filter, FileDown } from 'lucide-react';
import { useMemo } from 'react';
import { useAsync } from '../hooks/useAsync';
import { enterpriseApi, simulationApi } from '../api/client';
import type { PlantSupplyRiskDto, SupplyChainRiskReportResponse } from '../api/types';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { SeverityBreakdown } from '../components/SeverityBreakdown';
import { RecentActivity } from '../components/RecentActivity';

function labelSeverity(score: number): 'Critical' | 'High' | 'Medium' | 'Low' {
  if (score >= 0.5) return 'Critical';
  if (score >= 0.2) return 'High';
  if (score >= 0.05) return 'Medium';
  return 'Low';
}

function severityStyle(s: 'Critical' | 'High' | 'Medium' | 'Low') {
  const map = {
    Critical: 'var(--error)',
    High: 'var(--warning)',
    Medium: 'var(--success)',
    Low: 'var(--text-tertiary)',
  };
  return map[s];
}

function bucketPlants(plants: PlantSupplyRiskDto[]) {
  let critical = 0;
  let high = 0;
  let medium = 0;
  for (const p of plants) {
    const x = Math.max(p.plantRiskScore, p.disturbanceCertainty);
    if (x >= 0.5) critical++;
    else if (x >= 0.2) high++;
    else if (x > 0.05) medium++;
  }
  return { critical, high, medium };
}

/** Article titles surfaced by the simulation agent (from supplier exposure analysis). */
function articleSignalsFromSimulation(risk: SupplyChainRiskReportResponse | null) {
  const titles = new Set<string>();
  for (const p of risk?.plants ?? []) {
    for (const s of p.suppliers ?? []) {
      for (const t of s.contributingArticleTitles ?? []) {
        if (t?.trim()) titles.add(t.trim());
      }
    }
  }
  return [...titles].slice(0, 8).map((text) => ({
    color: 'var(--accent)' as const,
    text,
    time: 'from simulation report',
  }));
}

export function Disruptions() {
  const { data, loading, error } = useAsync(async () => {
    const [risk, enterprisePlants] = await Promise.all([
      simulationApi.supplyChainRiskReport(),
      enterpriseApi.listPlants(),
    ]);
    return { risk, enterprisePlantTotal: enterprisePlants.length };
  }, []);

  const risk = data?.risk;
  const plants = risk?.plants ?? [];
  const { critical, high, medium } = bucketPlants(plants);
  const activeSignals = plants.filter(
    (p) => p.plantRiskScore > 0.001 || p.disturbanceCertainty > 0.001
  ).length;

  const rows = useMemo(() => {
    return [...plants].sort((a, b) => b.plantRiskScore - a.plantRiskScore);
  }, [plants]);

  const recentItems = useMemo(() => articleSignalsFromSimulation(risk ?? null), [risk]);

  return (
    <Layout>
      <HeaderBar
        title="DISRUPTIONS"
        subtitle="Simulation agent (supply-chain-risk) + enterprise-backed plants/suppliers"
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
            value={loading ? '…' : data?.enterprisePlantTotal ?? 0}
            valueColor="primary"
            subtext="GET /api/v1/plants"
            subtextColor="primary"
          />
          <KPICard
            label="PLANTS IN SIMULATION REPORT"
            value={loading ? '…' : risk?.plantCount ?? 0}
            valueColor="primary"
          />
          <KPICard
            label="ACTIVE SIGNALS"
            value={loading ? '…' : activeSignals}
            valueColor={activeSignals > 0 ? 'error' : 'success'}
          />
          <KPICard
            label="PORTFOLIO RISK"
            value={loading ? '…' : (risk?.portfolioRiskScore ?? 0).toFixed(3)}
            valueColor="primary"
          />
          <KPICard
            label="REASONING ARTICLES (VIA SIMULATION)"
            value={loading ? '…' : risk?.reasoningArticleCount ?? 0}
            valueColor="info"
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
                PLANT RISK LOG
              </span>
              <div
                style={{
                  background: 'var(--error-dim)',
                  borderRadius: 4,
                  padding: '2px 8px',
                }}
              >
                <span
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 11,
                    fontWeight: 700,
                    color: 'var(--error)',
                  }}
                >
                  {loading ? '…' : rows.length} rows
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
                      SEVERITY
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
                      RISK
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
                      DISTURBANCE
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
                      HRS TO IMPACT
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row) => {
                    const score = Math.max(row.plantRiskScore, row.disturbanceCertainty);
                    const sev = labelSeverity(score);
                    return (
                      <tr
                        key={row.plantId ?? row.plantName}
                        style={{ borderBottom: '1px solid var(--border)' }}
                      >
                        <td
                          style={{
                            padding: '12px 16px',
                            color: 'var(--text-primary)',
                            fontWeight: 600,
                          }}
                        >
                          {row.plantName}
                        </td>
                        <td style={{ padding: '12px 16px' }}>
                          <span
                            style={{
                              padding: '2px 8px',
                              borderRadius: 4,
                              fontFamily: 'var(--font-mono)',
                              fontSize: 10,
                              fontWeight: 600,
                              background: `${severityStyle(sev)}20`,
                              color: severityStyle(sev),
                            }}
                          >
                            {sev}
                          </span>
                        </td>
                        <td
                          style={{
                            padding: '12px 16px',
                            fontFamily: 'var(--font-mono)',
                            fontSize: 11,
                            color: 'var(--text-secondary)',
                          }}
                        >
                          {(row.plantRiskScore * 100).toFixed(1)}%
                        </td>
                        <td
                          style={{
                            padding: '12px 16px',
                            fontFamily: 'var(--font-mono)',
                            fontSize: 11,
                            color: 'var(--text-secondary)',
                          }}
                        >
                          {(row.disturbanceCertainty * 100).toFixed(1)}%
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
                          {row.estimatedHoursToImpact != null
                            ? row.estimatedHoursToImpact.toFixed(0)
                            : '—'}
                        </td>
                      </tr>
                    );
                  })}
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
            <SeverityBreakdown critical={critical} high={high} medium={medium} />
            <RecentActivity items={recentItems} />
          </div>
        </div>
      </div>
    </Layout>
  );
}
