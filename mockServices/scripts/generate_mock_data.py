#!/usr/bin/env python3
"""
Generates mock_places.json, mock_vessels.json, mock_articles.json for mockServices.
Run from repo: python3 mockServices/scripts/generate_mock_data.py
"""
from __future__ import annotations

import json
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src" / "main" / "resources"


def fmt_coord(x: float) -> str:
    return f"{x:.6f}"


# Real-world style coordinates (approximate public references: major ports, chokepoints, hubs)
PLACES_RAW: list[tuple[str, str, float, float]] = [
    # Chokepoints & passages
    ("Strait of Hormuz", "CHOKEPOINT", 26.56, 56.25),
    ("Strait of Malacca", "CHOKEPOINT", 2.50, 101.20),
    ("Suez Canal", "CHOKEPOINT", 30.42, 32.35),
    ("Panama Canal", "CHOKEPOINT", 9.10, -79.68),
    ("Strait of Gibraltar", "CHOKEPOINT", 35.97, -5.60),
    ("Bab el-Mandeb", "CHOKEPOINT", 12.58, 43.45),
    ("Danish Straits", "CHOKEPOINT", 55.80, 10.50),
    ("Bosphorus", "CHOKEPOINT", 41.12, 29.05),
    ("Lombok Strait", "CHOKEPOINT", -8.50, 115.75),
    ("Cape of Good Hope", "CHOKEPOINT", -34.35, 18.47),
    ("Taiwan Strait", "CHOKEPOINT", 24.50, 119.50),
    ("English Channel", "CHOKEPOINT", 50.80, 1.60),
    ("Strait of Dover", "CHOKEPOINT", 51.00, 1.40),
    ("Korea Strait", "CHOKEPOINT", 34.00, 129.50),
    ("Mozambique Channel", "CHOKEPOINT", -16.00, 42.00),
    ("Sunda Strait", "CHOKEPOINT", -6.00, 105.50),
    ("Magellan Strait", "CHOKEPOINT", -53.50, -70.50),
    ("Northwest Passage", "CHOKEPOINT", 74.00, -94.00),
    ("Strait of Sicily", "CHOKEPOINT", 37.50, 12.50),
    ("Bering Strait", "CHOKEPOINT", 65.80, -169.00),
    # Major ports / cities (per common maritime references)
    ("Port of Shanghai", "PORT", 31.2304, 121.4737),
    ("Shanghai", "CITY", 31.2304, 121.4737),
    ("Port of Singapore", "PORT", 1.2644, 103.8222),
    ("Singapore City", "CITY", 1.3521, 103.8198),
    ("Singapore", "COUNTRY", 1.3521, 103.8198),
    ("Port of Rotterdam", "PORT", 51.9244, 4.4777),
    ("Rotterdam", "CITY", 51.9244, 4.4777),
    ("Port of Los Angeles", "PORT", 33.7542, -118.2165),
    ("Los Angeles", "CITY", 34.0522, -118.2437),
    ("Port of Long Beach", "PORT", 33.7542, -118.2165),
    ("Port of Hamburg", "PORT", 53.5511, 9.9937),
    ("Hamburg", "CITY", 53.5511, 9.9937),
    ("Port of Antwerp", "PORT", 51.2194, 4.4025),
    ("Antwerp", "CITY", 51.2194, 4.4025),
    ("Port of Busan", "PORT", 35.0951, 129.0389),
    ("Busan", "CITY", 35.1796, 129.0756),
    ("Port of Hong Kong", "PORT", 22.3193, 114.1694),
    ("Hong Kong", "CITY", 22.3193, 114.1694),
    ("Port of Ningbo-Zhoushan", "PORT", 29.8683, 121.5440),
    ("Port of Shenzhen", "PORT", 22.5431, 114.0579),
    ("Shenzhen", "CITY", 22.5431, 114.0579),
    ("Port of Qingdao", "PORT", 36.0671, 120.3826),
    ("Qingdao", "CITY", 36.0671, 120.3826),
    ("Port of Guangzhou", "PORT", 23.1291, 113.2644),
    ("Guangzhou", "CITY", 23.1291, 113.2644),
    ("Port of Tokyo", "PORT", 35.4437, 139.6380),
    ("Tokyo", "CITY", 35.6762, 139.6503),
    ("Port of Yokohama", "PORT", 35.4437, 139.6380),
    ("Yokohama", "CITY", 35.4437, 139.6503),
    ("Port of Kaohsiung", "PORT", 22.6273, 120.3014),
    ("Kaohsiung", "CITY", 22.6273, 120.3014),
    ("Port Klang", "PORT", 3.0319, 101.4186),
    ("Tanjung Pelepas", "PORT", 1.3667, 103.5500),
    ("Port of Colombo", "PORT", 6.9271, 79.8612),
    ("Colombo", "CITY", 6.9271, 79.8612),
    ("Jebel Ali", "PORT", 25.0247, 55.0442),
    ("Port Jebel Ali", "PORT", 25.0247, 55.0442),
    ("Dubai City", "CITY", 25.2600, 55.3000),
    ("Port of Salalah", "PORT", 16.9527, 54.0067),
    ("Salalah", "CITY", 17.0197, 54.0892),
    ("Port of Piraeus", "PORT", 37.9420, 23.6462),
    ("Piraeus", "CITY", 37.9420, 23.6462),
    ("Port of Felixstowe", "PORT", 51.9617, 1.3511),
    ("Port of Bremerhaven", "PORT", 53.5396, 8.5809),
    ("Port of New York and New Jersey", "PORT", 40.6681, -74.0451),
    ("New York City", "CITY", 40.7128, -74.0060),
    ("Port of Savannah", "PORT", 32.0809, -81.0912),
    ("Savannah", "CITY", 32.0809, -81.0912),
    ("Port of Houston", "PORT", 29.7313, -95.2678),
    ("Houston", "CITY", 29.7604, -95.3698),
    ("Port of Santos", "PORT", -23.9608, -46.3336),
    ("Santos", "CITY", -23.9608, -46.3336),
    ("Port of Durban", "PORT", -29.8587, 31.0218),
    ("Durban", "CITY", -29.8587, 31.0218),
    ("Port of Melbourne", "PORT", -37.8136, 144.9631),
    ("Melbourne", "CITY", -37.8136, 144.9631),
    ("Port of Sydney", "PORT", -33.8688, 151.2093),
    ("Sydney", "CITY", -33.8688, 151.2093),
    ("Port of Algeciras", "PORT", 36.1408, -5.4526),
    ("Algeciras", "CITY", 36.1408, -5.4526),
    ("Port of Le Havre", "PORT", 49.4944, 0.1079),
    ("Le Havre", "CITY", 49.4944, 0.1079),
    ("Port of Gothenburg", "PORT", 57.7089, 11.9746),
    ("Gothenburg", "CITY", 57.7089, 11.9746),
    ("Port of Oslo", "PORT", 59.9139, 10.7522),
    ("Oslo", "CITY", 59.9139, 10.7522),
    ("Port of Mumbai", "PORT", 18.9582, 72.8424),
    ("Mumbai", "CITY", 19.0760, 72.8777),
    ("Port of Chennai", "PORT", 13.0827, 80.2707),
    ("Chennai", "CITY", 13.0827, 80.2707),
    ("Port of Jakarta", "PORT", -6.2088, 106.8456),
    ("Jakarta", "CITY", -6.2088, 106.8456),
    ("Port of Manila", "PORT", 14.5995, 120.9842),
    ("Manila", "CITY", 14.5995, 120.9842),
    ("Port of Ho Chi Minh City", "PORT", 10.7769, 106.7009),
    ("Ho Chi Minh City", "CITY", 10.7769, 106.7009),
    ("Port of Bangkok", "PORT", 13.6900, 100.4930),
    ("Bangkok", "CITY", 13.7563, 100.5018),
    ("Port of Seattle", "PORT", 47.6062, -122.3321),
    ("Seattle", "CITY", 47.6062, -122.3321),
    ("Port of Vancouver", "PORT", 49.2827, -123.1207),
    ("Vancouver", "CITY", 49.2827, -123.1207),
    ("Port of Montreal", "PORT", 45.5017, -73.5673),
    ("Montreal", "CITY", 45.5017, -73.5673),
    ("Port of Genoa", "PORT", 44.4056, 8.9463),
    ("Genoa", "CITY", 44.4056, 8.9463),
    ("Port of Barcelona", "PORT", 41.3851, 2.1734),
    ("Barcelona", "CITY", 41.3851, 2.1734),
    ("Port of Valencia", "PORT", 39.4699, -0.3763),
    ("Valencia", "CITY", 39.4699, -0.3763),
    ("Port of Lisbon", "PORT", 38.7223, -9.1393),
    ("Lisbon", "CITY", 38.7223, -9.1393),
    ("Port of Casablanca", "PORT", 33.5731, -7.5898),
    ("Casablanca", "CITY", 33.5731, -7.5898),
    ("Port of Alexandria", "PORT", 31.2001, 29.9187),
    ("Alexandria", "CITY", 31.2001, 29.9187),
    ("Port of Haifa", "PORT", 32.7940, 34.9896),
    ("Haifa", "CITY", 32.7940, 34.9896),
    ("Port of Jeddah", "PORT", 21.4858, 39.1925),
    ("Jeddah", "CITY", 21.4858, 39.1925),
    ("Port of Dammam", "PORT", 26.4207, 50.0888),
    ("Dammam", "CITY", 26.4207, 50.0888),
    ("Port of Karachi", "PORT", 24.8607, 66.9905),
    ("Karachi", "CITY", 24.8607, 66.9905),
    ("Port of Chittagong", "PORT", 22.3569, 91.7832),
    ("Chittagong", "CITY", 22.3569, 91.7832),
    ("Port of Fremantle", "PORT", -32.0569, 115.7439),
    ("Fremantle", "CITY", -32.0569, 115.7439),
    ("Port of Auckland", "PORT", -36.8485, 174.7633),
    ("Auckland", "CITY", -36.8485, 174.7633),
    # Countries / regions already partially covered
    ("United Arab Emirates", "COUNTRY", 24.4600, 54.3700),
    ("Oman", "COUNTRY", 21.0000, 57.0000),
    ("Bahrain", "COUNTRY", 26.0600, 50.5500),
    ("Egypt", "COUNTRY", 26.8200, 30.8000),
    ("Panama", "COUNTRY", 8.5300, -80.7800),
    ("South Africa", "COUNTRY", -30.5500, 22.9300),
    ("Japan", "COUNTRY", 36.2000, 138.2500),
    ("South Korea", "COUNTRY", 35.9000, 127.8000),
    ("China", "COUNTRY", 35.0000, 105.0000),
    ("United States", "COUNTRY", 39.0000, -98.0000),
    ("Germany", "COUNTRY", 51.0000, 10.0000),
    ("Netherlands", "COUNTRY", 52.0000, 5.0000),
    ("United Kingdom", "COUNTRY", 54.0000, -2.0000),
    ("France", "COUNTRY", 46.0000, 2.0000),
    ("India", "COUNTRY", 20.6000, 78.9000),
    ("Brazil", "COUNTRY", -14.2000, -51.9000),
    ("Australia", "COUNTRY", -25.0000, 133.0000),
    ("Canada", "COUNTRY", 56.0000, -106.0000),
    ("Mexico", "COUNTRY", 23.0000, -102.0000),
    ("Turkey", "COUNTRY", 39.0000, 35.0000),
    ("Saudi Arabia", "COUNTRY", 24.0000, 45.0000),
    ("Indonesia", "COUNTRY", -2.0000, 118.0000),
    ("Malaysia", "COUNTRY", 4.0000, 102.0000),
    ("Vietnam", "COUNTRY", 14.0000, 108.0000),
    ("Philippines", "COUNTRY", 12.0000, 122.0000),
    ("Taiwan", "COUNTRY", 23.7000, 121.0000),
    ("Chile", "COUNTRY", -35.0000, -71.0000),
    ("Argentina", "COUNTRY", -34.0000, -64.0000),
    ("Nigeria", "COUNTRY", 9.0000, 8.0000),
    ("Kenya", "COUNTRY", -1.0000, 38.0000),
    ("Norway", "COUNTRY", 62.0000, 10.0000),
    ("Sweden", "COUNTRY", 62.0000, 15.0000),
    ("Poland", "COUNTRY", 52.0000, 19.0000),
    ("Italy", "COUNTRY", 42.0000, 12.0000),
    ("Spain", "COUNTRY", 40.0000, -4.0000),
]


def build_places() -> list[dict]:
    out = []
    for name, ptype, lat, lon in PLACES_RAW:
        out.append(
            {
                "name": name,
                "type": ptype,
                "latitude": fmt_coord(lat),
                "longitude": fmt_coord(lon),
            }
        )
    return out


VESSEL_PREFIXES = [
    "COSCO",
    "MAERSK",
    "MSC",
    "EVER",
    "CMA CGM",
    "HAPAG",
    "ONE",
    "YANG MING",
    "ZIM",
    "HMM",
    "OOCL",
    "APL",
    "NYK",
    "MOL",
    "K-LINE",
    "PIL",
    "WAN HAI",
    "X-PRESS",
    "ATLANTIC",
    "PACIFIC",
    "NORDIC",
    "ARCTIC",
    "BLUE STAR",
    "RED SEA",
    "GULF",
]


# Regional seed centers (lat, lon) — major shipping lanes / clusters
REGION_CENTERS = [
    (45.06, -8.90),  # original Iberian cluster
    (25.25, 55.25),  # UAE / Gulf
    (1.30, 103.80),  # Singapore
    (31.20, 121.50),  # Shanghai
    (51.92, 4.48),  # Rotterdam
    (35.10, 129.05),  # Busan
    (22.30, 114.15),  # Hong Kong
    (29.87, 121.55),  # Ningbo area
    (33.75, -118.25),  # LA/LB
    (40.67, -74.05),  # NY/NJ
    (30.42, 32.35),  # Suez
    (9.10, -79.70),  # Panama
    (26.56, 56.25),  # Hormuz
    (2.50, 101.20),  # Malacca
    (35.97, -5.60),  # Gibraltar
    (-34.35, 18.47),  # Cape of Good Hope
    (37.94, 23.65),  # Piraeus
    (-23.96, -46.33),  # Santos
    (19.08, 72.88),  # Mumbai
    (-33.87, 151.21),  # Sydney
]


def build_vessels(count: int = 520, seed: int = 42) -> list[dict]:
    rng = random.Random(seed)
    vessels: list[dict] = []
    for i in range(count):
        center = REGION_CENTERS[i % len(REGION_CENTERS)]
        # scatter within ~450 km for variety
        dlat = rng.uniform(-4.0, 4.0)
        dlon = rng.uniform(-4.0, 4.0)
        lat = max(-85, min(85, center[0] + dlat))
        lon = max(-179.9, min(179.9, center[1] + dlon))
        prefix = rng.choice(VESSEL_PREFIXES)
        name = f"{prefix} {rng.choice(['STAR', 'TRADER', 'EXPRESS', 'FEEDER', 'VOYAGER', 'EAGLE', 'HERON', 'ORION'])} {rng.randint(1, 999)}"
        mmsi = str(210000000 + i)
        speed = str(rng.randint(0, 160))
        course = str(rng.randint(0, 359))
        heading = str(rng.randint(0, 359)) if rng.random() > 0.08 else None
        vessels.append(
            {
                "mmsi": mmsi,
                "name": name,
                "latitude": fmt_coord(lat),
                "longitude": fmt_coord(lon),
                "speed": speed,
                "course": course,
                "heading": heading,
            }
        )
    # Keep legacy names near first cluster for backward compatibility
    legacy = [
        {
            "mmsi": "245297000",
            "name": "ELBEBORG",
            "latitude": "45.078705",
            "longitude": "-8.9553804",
            "speed": "110",
            "course": "185",
            "heading": "184",
        },
        {
            "mmsi": "205106000",
            "name": "MARCUS",
            "latitude": "45.041000",
            "longitude": "-8.8073854",
            "speed": "35",
            "course": "198",
            "heading": None,
        },
        {
            "mmsi": "305005300",
            "name": "WANHEIM",
            "latitude": "45.070534",
            "longitude": "-8.8638268",
            "speed": "101",
            "course": "354",
            "heading": "353",
        },
        {
            "mmsi": "249223000",
            "name": "CGAS JAGUAR",
            "latitude": "45.048882",
            "longitude": "-8.8798838",
            "speed": "74",
            "course": "357",
            "heading": "357",
        },
    ]
    # Prepend legacy so tests expecting these names still find them
    return legacy + vessels


def article_template(i: int, places_sample: list[str]) -> dict:
    """Build a news article with embedded geography for reasoning-agent."""
    cities_line = ", ".join(places_sample[:6])
    title_themes = [
        "Supply chain strain hits semiconductor flows",
        "Port congestion ripples through trans-Pacific lanes",
        "Energy transition reshapes bunker and LNG logistics",
        "Cyber incident tests port community systems",
        "Strike threat looms over coastal warehousing hubs",
        "Tariff uncertainty redirects trans-shipment patterns",
        "Severe weather disrupts coastal manufacturing clusters",
        "Raw material squeeze tightens just-in-time networks",
        "Red Sea security premium lifts insurance on Asia–Europe trade",
        "Container imbalance strains inland rail connectors",
    ]
    title = f"{title_themes[i % len(title_themes)]} — dispatch {i + 1}"
    body = (
        f"Analysts monitoring {cities_line} report cascading effects across trucking, rail, and ocean legs. "
        f"Forwarders cite renewed focus on the Strait of Malacca and the Suez Canal as schedule reliability remains volatile. "
        f"Shippers routing via Singapore and Rotterdam are evaluating alternate hubs including Jebel Ali and Piraeus while "
        f"insurance desks track developments near the Strait of Hormuz and the Panama Canal. "
        f"In Asia, cargo managers highlight Shanghai, Busan, and Hong Kong as bellwethers; in Europe, Hamburg and Antwerp face "
        f"berth competition. North American flows through Los Angeles and Houston remain sensitive to labor availability. "
        f"Stakeholders in Dubai City and Mumbai emphasized cross-border customs alignment. "
        f"Observers also note monitoring of Bab el-Mandeb and the Strait of Gibraltar alongside Cape of Good Hope diversions. "
        f"Technology teams warned ransomware could freeze yard operations from Seattle to Vancouver. "
        f"The report underscores links between Taiwan Strait sentiment and charter markets, while South Korea and Japan "
        f"importers watch bunker spreads. Latin American gateways such as Santos and Pacific Northwest routes via Vancouver "
        f"round out the risk map for the quarter.\n\n"
        f"Participants at a Geneva logistics forum argued that United Arab Emirates ports remain pivotal for Gulf transshipment, "
        f"while Egypt and Oman feature in contingency mapping. United States west coast terminals coordinate with Canada "
        f"intermodal partners; Germany and Netherlands hubs synchronize with United Kingdom feeder services. "
        f"China and India manufacturing demand continues to pull containers through Malaysia and Indonesia transshipment nodes."
    )
    src = f"maritime-brief-{i % 50}.com"
    return {
        "uri": str(2000000001 + i),
        "lang": "eng",
        "isDuplicate": False,
        "date": "2026-03-15",
        "time": f"{(10 + (i % 8)):02d}:00:00",
        "dateTime": f"2026-03-15T{(10 + (i % 8)):02d}:00:00Z",
        "dateTimePub": f"2026-03-15T{(10 + (i % 8)):02d}:00:00Z",
        "dataType": "news",
        "sim": 0,
        "url": f"https://www.{src}/article/{2000000001 + i}",
        "title": title,
        "body": body,
        "source": {"uri": src, "dataType": "news", "title": f"Global Maritime Brief {i % 50}"},
        "authors": [],
        "image": f"https://www.images.com/maritime-{i % 100}.jpg",
        "eventUri": None,
        "sentiment": -0.1 + (i % 5) * 0.05,
        "wgt": 511000001 + i,
        "relevance": 100,
    }


def build_articles(n: int = 48) -> dict:
    # Names that exist in PLACES_RAW — rotate for variety
    key_places = [
        "Strait of Malacca",
        "Suez Canal",
        "Singapore City",
        "Rotterdam",
        "Shanghai",
        "Hong Kong",
        "Busan",
        "Los Angeles",
        "Houston",
        "Panama Canal",
        "Strait of Hormuz",
        "Dubai City",
        "Mumbai",
        "Piraeus",
        "Santos",
        "Vancouver",
        "Hamburg",
        "Antwerp",
        "Tokyo",
        "Colombo",
        "Jebel Ali",
        "Strait of Gibraltar",
        "Bab el-Mandeb",
        "Cape of Good Hope",
        "Taiwan Strait",
        "United Arab Emirates",
        "Oman",
        "Egypt",
        "United States",
        "Germany",
        "Netherlands",
        "China",
        "India",
        "Malaysia",
        "Indonesia",
    ]
    results = []
    for i in range(n):
        # rotate slices so each article mentions a different mix
        chunk = [key_places[(i + j) % len(key_places)] for j in range(12)]
        results.append(article_template(i, chunk))
    return {
        "articles": {
            "count": n,
            "page": 1,
            "pages": 1,
            "totalResults": n,
            "results": results,
        }
    }


def main() -> None:
    places = build_places()
    vessels = build_vessels()
    articles = build_articles(48)

    RES.mkdir(parents=True, exist_ok=True)
    (RES / "mock_places.json").write_text(json.dumps(places, indent=2), encoding="utf-8")
    (RES / "mock_vessels.json").write_text(json.dumps(vessels, indent=2), encoding="utf-8")
    (RES / "mock_articles.json").write_text(json.dumps(articles, indent=2), encoding="utf-8")
    print(f"Wrote {len(places)} places, {len(vessels)} vessels, {articles['articles']['count']} articles -> {RES}")


if __name__ == "__main__":
    main()
