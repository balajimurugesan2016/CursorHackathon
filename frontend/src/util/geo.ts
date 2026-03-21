/** Parse enterprise lat/lon strings to numbers; returns null if invalid. */
export function parseCoord(s: string | null | undefined): number | null {
  if (s == null || s.trim() === '') return null;
  const n = Number.parseFloat(s);
  return Number.isFinite(n) ? n : null;
}

/** Project WGS84 to SVG viewBox coordinates (simple equirectangular). */
export function projectToSvg(lat: number, lon: number, width: number, height: number) {
  const x = ((lon + 180) / 360) * width;
  const y = ((90 - lat) / 180) * height;
  return { x, y };
}
