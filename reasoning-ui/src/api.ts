import type { ReasoningReportResponse, SupplyChainRiskReportResponse } from "./types";

const API_BASE = (import.meta.env.VITE_API_BASE as string | undefined) ?? "";
const SUPPLY_RISK_BASE = (import.meta.env.VITE_SUPPLY_RISK_BASE as string | undefined) ?? "";

/**
 * In Vite dev, use same-origin `/api/...` so the dev-server proxy can route to 8094 without CORS.
 * For `vite preview` (no proxy), fall back to direct 8094 (CORS enabled on the agent).
 */
function resolveSupplyRiskBase(): string {
  if (SUPPLY_RISK_BASE) return SUPPLY_RISK_BASE;
  if (import.meta.env.DEV) return "";
  if (typeof window !== "undefined") {
    const { hostname, port } = window.location;
    if ((hostname === "localhost" || hostname === "127.0.0.1") && port === "4173") {
      return "http://localhost:8094";
    }
  }
  return "";
}

export async function fetchReasoningReport(radiusNm: number): Promise<ReasoningReportResponse> {
  const params = new URLSearchParams();
  params.set("radiusNm", String(radiusNm));
  const res = await fetch(`${API_BASE}/api/agent/reasoning-report?${params.toString()}`, {
    headers: { Accept: "application/json" },
  });
  if (!res.ok) {
    const text = await res.text();
    let msg = `HTTP ${res.status}`;
    try {
      const j = JSON.parse(text) as { message?: string };
      if (j.message) msg = j.message;
    } catch {
      if (text) msg = text.slice(0, 200);
    }
    throw new Error(msg);
  }
  return res.json() as Promise<ReasoningReportResponse>;
}

export async function fetchSupplyChainRiskReport(radiusNm: number): Promise<SupplyChainRiskReportResponse> {
  const params = new URLSearchParams();
  params.set("radiusNm", String(radiusNm));
  const res = await fetch(
    `${resolveSupplyRiskBase()}/api/agent/supply-chain-risk-report?${params.toString()}`,
    {
      headers: { Accept: "application/json" },
    }
  );
  if (!res.ok) {
    const text = await res.text();
    let msg = `HTTP ${res.status}`;
    try {
      const j = JSON.parse(text) as { message?: string };
      if (j.message) msg = j.message;
    } catch {
      if (text) msg = text.slice(0, 200);
    }
    throw new Error(msg);
  }
  return res.json() as Promise<SupplyChainRiskReportResponse>;
}
