import { useEffect, useState } from 'react';
import type { Plant, ProbabilityResponse, Supplier } from '../api/types';
import { locationMatches } from '../util/locationMatch';

const CRITICAL_THRESHOLD = 70;

export function useMatchedSupplyChain(
  probability: ProbabilityResponse | null,
  suppliers: Supplier[] | null,
  plants: Plant[] | null
): { matchedSuppliers: Supplier[]; plantsAtRisk: Plant[]; loading: boolean } {
  const [matchedSuppliers, setMatchedSuppliers] = useState<Supplier[]>([]);
  const [plantsAtRisk, setPlantsAtRisk] = useState<Plant[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!probability?.items?.length || !suppliers?.length) {
      setMatchedSuppliers([]);
      setPlantsAtRisk([]);
      setLoading(false);
      return;
    }

    setLoading(true);
    Promise.resolve().then(() => {
      const criticalItems = probability.items.filter(
        (i) => i.probabilityPercent > CRITICAL_THRESHOLD
      );
      const probLocations = [
        ...new Set(
          criticalItems.flatMap((i) => i.locations ?? []).filter(Boolean)
        ),
      ];

      if (probLocations.length === 0) {
        setMatchedSuppliers([]);
        setPlantsAtRisk([]);
        setLoading(false);
        return;
      }

      const matched = suppliers.filter((s) =>
        locationMatches(s.location, probLocations)
      );

      const plantIds = new Set<number>();
      const atRisk: Plant[] = [];
      const plantsById = plants?.length ? new Map(plants.map((p) => [p.id, p])) : null;
      for (const s of matched) {
        const linked = s.plants ?? [];
        for (const p of linked) {
          if (p?.id == null) continue;
          if (plantIds.has(p.id)) continue;
          plantIds.add(p.id);
          const full = plantsById?.get(p.id) ?? p;
          atRisk.push(full);
        }
      }

      setMatchedSuppliers(matched);
      setPlantsAtRisk(atRisk);
      setLoading(false);
    });
  }, [probability, suppliers, plants]);

  return { matchedSuppliers, plantsAtRisk, loading };
}
