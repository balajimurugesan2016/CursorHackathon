#!/usr/bin/env python3
"""
Generates mock_plants.json for enterpriseservice from city locations.
Run from repo: python3 enterpriseservice/scripts/generate_mock_plants.py
"""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src" / "main" / "resources"


def fmt_coord(x: float) -> str:
    return f"{x:.6f}"


# City locations from mock_places (CITY type only, mostly Europe) — excludes chokepoints
EUROPEAN_CITIES = [
    ("Rotterdam", 51.9244, 4.4777),
    ("Hamburg", 53.5511, 9.9937),
    ("Antwerp", 51.2194, 4.4025),
    ("Piraeus", 37.9420, 23.6462),
    ("Algeciras", 36.1408, -5.4526),
    ("Le Havre", 49.4944, 0.1079),
    ("Gothenburg", 57.7089, 11.9746),
    ("Oslo", 59.9139, 10.7522),
    ("Genoa", 44.4056, 8.9463),
    ("Barcelona", 41.3851, 2.1734),
    ("Valencia", 39.4699, -0.3763),
    ("Lisbon", 38.7223, -9.1393),
    ("Singapore City", 1.3521, 103.8198),
    ("Shanghai", 31.2304, 121.4737),
]

PLANT_SUFFIXES = [
    "Assembly Plant",
    "Manufacturing Center",
    "Production Facility",
    "Processing Plant",
    "Distribution Hub",
    "Fabrication Plant",
]


def build_plants() -> list[dict]:
    """Build mock plant data (Plant entity shape)."""
    plants = []
    statuses = ["ACTIVE", "ACTIVE", "ACTIVE", "STANDBY", "MAINTENANCE"]
    for i, (name, lat, lon) in enumerate(EUROPEAN_CITIES):
        suffix = PLANT_SUFFIXES[i % len(PLANT_SUFFIXES)]
        total_lines = (i % 4 + 2) * 2
        lines_active = max(0, total_lines - (i % 3))
        capacity = 65.0 + (i * 2.5) % 35.0
        plants.append({
            "plantName": f"{name} {suffix}",
            "location": name,
            "latitude": fmt_coord(lat),
            "longitude": fmt_coord(lon),
            "status": statuses[i % len(statuses)],
            "capacityPct": round(capacity, 1),
            "totalLines": total_lines,
            "linesActive": lines_active,
        })
    return plants


def main() -> None:
    plants = build_plants()
    RES.mkdir(parents=True, exist_ok=True)
    out_path = RES / "mock_plants.json"
    out_path.write_text(json.dumps(plants, indent=2), encoding="utf-8")
    print(f"Wrote {len(plants)} plants -> {out_path}")


if __name__ == "__main__":
    main()
