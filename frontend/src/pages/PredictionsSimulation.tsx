import { useState } from 'react';
import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';
import { KPICard } from '../components/KPICard';
import { WorldMap } from '../components/WorldMap';
import { DataModeToggle } from '../components/DataModeToggle';
import { EventProbabilityCard } from '../components/EventProbabilityCard';

const EVENT_CARDS = [
  {
    badge: 'CRITICAL',
    badgeColor: 'var(--error)',
    percentage: 87,
    title: 'Port of Shanghai Congestion',
    description:
      'Extended delays expected for 2-3 weeks. Affects 34 active shipments across APAC region.',
  },
  {
    badge: 'HIGH',
    badgeColor: 'var(--warning)',
    percentage: 72,
    title: 'European Rail Strike',
    description:
      'Transit disruption across DE-FR corridor. 18 supplier routes impacted for chemical transport.',
  },
  {
    badge: 'MEDIUM',
    badgeColor: '#F59E0B',
    percentage: 54,
    title: 'TiO₂ Supply Shortage',
    description:
      'Titanium dioxide availability declining. Major supplier in Australia reducing output by 30%.',
  },
  {
    badge: 'LOW',
    badgeColor: 'var(--accent)',
    percentage: 28,
    title: 'Gulf Hurricane Season',
    description:
      'Potential disruption to Houston plant logistics. Early models show moderate tropical activity.',
  },
];

export function PredictionsSimulation() {
  const [mode, setMode] = useState<'real' | 'simulation'>('real');

  return (
    <Layout>
      <HeaderBar
        title="PREDICTIONS & SIMULATION"
        subtitle="AI-powered event probability analysis and supply chain scenario modeling."
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
        {mode === 'simulation' && (
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
                  AI GENERATED
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
              {EVENT_CARDS.map((card) => (
                <EventProbabilityCard
                  key={card.title}
                  badge={card.badge}
                  badgeColor={card.badgeColor}
                  percentage={card.percentage}
                  title={card.title}
                  description={card.description}
                />
              ))}
            </div>
          </div>
        )}

        <div
          style={{
            display: 'flex',
            gap: 16,
            flexWrap: 'wrap',
          }}
        >
          <KPICard label="DEMAND FORECAST (30D)" value="+12.4%" valueColor="success" />
          <KPICard label="SUPPLY RISK SCORE" value="0.23" valueColor="primary" />
          <KPICard label="ACTIVE SCENARIOS" value="8" valueColor="primary" />
          <KPICard label="MODEL ACCURACY" value="94.1%" valueColor="success" />
        </div>

        <div
          style={{
            flex: 1,
            display: 'flex',
            gap: 16,
            minHeight: 0,
          }}
        >
          <WorldMap />

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
              SIMULATION CONTROLS
            </span>
            <div
              style={{
                display: 'flex',
                flexDirection: 'column',
                gap: 12,
              }}
            >
              <div
                style={{
                  padding: '12px 16px',
                  background: 'var(--bg-surface)',
                  borderRadius: 4,
                  borderLeft: '3px solid var(--accent)',
                }}
              >
                <span
                  style={{
                    fontFamily: 'var(--font-sans)',
                    fontSize: 13,
                    fontWeight: 600,
                    color: 'var(--text-primary)',
                  }}
                >
                  Baseline Scenario
                </span>
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    color: 'var(--text-secondary)',
                    marginTop: 4,
                  }}
                >
                  Current demand model · No disruptions
                </div>
              </div>
              <div
                style={{
                  padding: '12px 16px',
                  background: 'var(--bg-surface)',
                  borderRadius: 4,
                  borderLeft: '3px solid var(--text-tertiary)',
                }}
              >
                <span
                  style={{
                    fontFamily: 'var(--font-sans)',
                    fontSize: 13,
                    fontWeight: 600,
                    color: 'var(--text-primary)',
                  }}
                >
                  Stress Test — Supply Shock
                </span>
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    color: 'var(--text-secondary)',
                    marginTop: 4,
                  }}
                >
                  Simulate 20% supplier loss
                </div>
              </div>
              <div
                style={{
                  padding: '12px 16px',
                  background: 'var(--bg-surface)',
                  borderRadius: 4,
                  borderLeft: '3px solid var(--text-tertiary)',
                }}
              >
                <span
                  style={{
                    fontFamily: 'var(--font-sans)',
                    fontSize: 13,
                    fontWeight: 600,
                    color: 'var(--text-primary)',
                  }}
                >
                  Demand Surge — Q4 Peak
                </span>
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    color: 'var(--text-secondary)',
                    marginTop: 4,
                  }}
                >
                  +35% demand forecast
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}
