import type { ReasoningReportResponse } from "./types";

const API_BASE = (import.meta.env.VITE_API_BASE as string | undefined) ?? "";

export async function fetchReasoningReport(): Promise<ReasoningReportResponse> {
  const res = await fetch(`${API_BASE}/api/agent/reasoning-report`, {
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
