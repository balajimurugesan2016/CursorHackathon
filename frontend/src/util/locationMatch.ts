/** Normalize string for comparison: lowercase, trim. */
function normalize(s: string): string {
  return (s ?? '').toLowerCase().trim();
}

/**
 * Check if supplier location matches any probability location.
 * Uses flexible matching: contains (either direction) or exact match after normalization.
 * E.g. "Dubai" in probability matches "Dubai City" in supplier.
 */
export function locationMatches(
  supplierLocation: string | null | undefined,
  probabilityLocations: string[]
): boolean {
  const loc = normalize(supplierLocation ?? '');
  if (!loc) return false;

  for (const prob of probabilityLocations) {
    const p = normalize(prob);
    if (!p) continue;
    if (loc === p) return true;
    if (loc.includes(p)) return true;
    if (p.includes(loc)) return true;
  }
  return false;
}
