import type { Plant, Supplier, Shipment, ProbabilityResponse } from './types';

async function json<T>(path: string): Promise<T> {
  const res = await fetch(path);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`${res.status} ${path}${text ? `: ${text.slice(0, 200)}` : ''}`);
  }
  return res.json() as Promise<T>;
}

/** Master data — enterpriseservice (:8085). */
export const enterpriseApi = {
  listPlants: () => json<Plant[]>('/api/v1/plants'),
  listSuppliers: () => json<Supplier[]>('/api/v1/suppliers'),
  listShipments: () => json<Shipment[]>('/api/v1/shipments'),
};

/**
 * Event probability — probability-service (:8097).
 * Pushed via WebSocket; REST fallback returns cached value.
 */
export const probabilityApi = {
  getProbabilities: () => json<ProbabilityResponse>('/api/probability'),
};
