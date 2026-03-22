import { useEffect, useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap, ZoomControl } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import 'leaflet.geodesic';
import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { parseCoord } from '../util/geo';
const plantIcon = L.divIcon({
  className: 'supply-network-marker',
  html: '<span style="width:12px;height:12px;border-radius:50%;background:#22D3EE;display:block;border:2px solid #0A0F1C;"></span>',
  iconSize: [12, 12],
  iconAnchor: [6, 6],
});

const supplierIcon = L.divIcon({
  className: 'supply-network-marker',
  html: '<span style="width:12px;height:12px;border-radius:50%;background:#F59E0B;display:block;border:2px solid #0A0F1C;"></span>',
  iconSize: [12, 12],
  iconAnchor: [6, 6],
});

function MapBounds({ points }: { points: { lat: number; lng: number }[] }) {
  const map = useMap();
  useEffect(() => {
    if (points.length === 0) return;
    const bounds = L.latLngBounds(points.map((p) => [p.lat, p.lng] as L.LatLngTuple));
    map.fitBounds(bounds, { padding: [40, 40], maxZoom: 6 });
  }, [map, points]);
  return null;
}

type Link = { from: L.LatLngExpression; to: L.LatLngExpression };

function buildLinks(
  shipments: { suppliers?: { id: number }[] | null; plants?: { id: number }[] | null }[] | null | undefined,
  plantByGlob: Map<number, { lat: number; lng: number }>,
  supplierByGlob: Map<number, { lat: number; lng: number }>
): Link[] {
  const seen = new Set<string>();
  const result: Link[] = [];
  const list = shipments ?? [];
  for (const sh of list) {
    for (const plant of sh.plants ?? []) {
      const pc = plantByGlob.get(plant.id);
      if (!pc) continue;
      for (const supplier of sh.suppliers ?? []) {
        const sc = supplierByGlob.get(supplier.id);
        if (!sc) continue;
        const key = `${plant.id}-${supplier.id}`;
        if (seen.has(key)) continue;
        seen.add(key);
        result.push({ from: [sc.lat, sc.lng], to: [pc.lat, pc.lng] });
      }
    }
  }
  return result;
}

function GeodesicLines({ links }: { links: Link[] }) {
  const map = useMap();
  useEffect(() => {
    if (links.length === 0) return;
    const GeodesicClass = (L as unknown as { Geodesic: new (coords: L.LatLngExpression[], options?: L.PolylineOptions) => L.Polyline }).Geodesic;
    if (!GeodesicClass) return;
    const layers: L.Polyline[] = [];
    for (const link of links) {
      const line = new GeodesicClass([link.from, link.to], {
        color: 'rgba(34, 211, 238, 0.4)',
        weight: 1.5,
      });
      line.addTo(map);
      layers.push(line);
    }
    return () => layers.forEach((l) => map.removeLayer(l));
  }, [map, links]);
  return null;
}

export function SupplyNetworkMap() {
  const { data: plants, loading: loadingPlants, error } = useAsync(() => enterpriseApi.listPlants(), []);
  const { data: suppliers } = useAsync(() => enterpriseApi.listSuppliers(), []);
  const { data: shipments } = useAsync(() => enterpriseApi.listShipments(), []);

  const plantMarkers = useMemo(() => {
    return (
      plants?.flatMap((p) => {
        const lat = parseCoord(p.latitude);
        const lon = parseCoord(p.longitude);
        if (lat == null || lon == null) return [];
        return [{ id: p.id, name: p.plantName, lat, lng: lon }];
      }) ?? []
    );
  }, [plants]);

  const supplierMarkers = useMemo(() => {
    return (
      suppliers?.flatMap((s) => {
        const lat = parseCoord(s.latitude);
        const lon = parseCoord(s.longitude);
        if (lat == null || lon == null) return [];
        return [{ id: s.id, name: s.supplierName, lat, lng: lon }];
      }) ?? []
    );
  }, [suppliers]);

  const links = useMemo(() => {
    const plantByGlob = new Map(plantMarkers.map((p) => [p.id, { lat: p.lat, lng: p.lng }]));
    const supplierByGlob = new Map(supplierMarkers.map((s) => [s.id, { lat: s.lat, lng: s.lng }]));
    return buildLinks(shipments, plantByGlob, supplierByGlob);
  }, [shipments, plantMarkers, supplierMarkers]);

  const allPoints = useMemo(
    () => [
      ...plantMarkers.map((m) => ({ lat: m.lat, lng: m.lng })),
      ...supplierMarkers.map((m) => ({ lat: m.lat, lng: m.lng })),
    ],
    [plantMarkers, supplierMarkers]
  );

  const loading = loadingPlants;
  const hasData = plantMarkers.length > 0 || supplierMarkers.length > 0;

  return (
    <div
      style={{
        flex: 1,
        minWidth: 0,
        background: 'var(--bg-card)',
        borderRadius: 4,
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
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
          GLOBAL SUPPLY NETWORK
        </span>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 6,
                height: 6,
                borderRadius: '50%',
                background: 'var(--accent)',
              }}
            />
            <span
              style={{
                fontFamily: 'var(--font-mono)',
                fontSize: 10,
                fontWeight: 500,
                color: 'var(--text-secondary)',
              }}
            >
              Plants
            </span>
          </div>
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 6,
                height: 6,
                borderRadius: '50%',
                background: 'var(--warning)',
              }}
            />
            <span
              style={{
                fontFamily: 'var(--font-mono)',
                fontSize: 10,
                fontWeight: 500,
                color: 'var(--text-secondary)',
              }}
            >
              Suppliers
            </span>
          </div>
        </div>
      </div>

      <div
        style={{
          flex: 1,
          background: 'var(--bg-surface)',
          position: 'relative',
          minHeight: 300,
        }}
      >
        <div style={{ position: 'absolute', inset: 0, zIndex: 0 }}>
          <MapContainer
            center={[20, 20]}
            zoom={2}
            style={{ height: '100%', width: '100%', minHeight: 300 }}
            zoomControl={false}
          >
            <ZoomControl position="bottomright" />
            <TileLayer
              attribution='&copy; <a href="https://carto.com/">CARTO</a>'
              url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
            />
            <MapBounds points={allPoints} />
            <GeodesicLines links={links} />
            {supplierMarkers.map((s) => (
              <Marker key={`sup-${s.id}`} position={[s.lat, s.lng]} icon={supplierIcon}>
                <Popup>
                  <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-primary)' }}>
                    <strong>{s.name}</strong> — Supplier
                  </div>
                </Popup>
              </Marker>
            ))}
            {plantMarkers.map((p) => (
              <Marker key={`plt-${p.id}`} position={[p.lat, p.lng]} icon={plantIcon}>
                <Popup>
                  <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-primary)' }}>
                    <strong>{p.name}</strong> — Plant
                  </div>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </div>
        {error && (
          <div
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              padding: 16,
              color: 'var(--error)',
              fontFamily: 'var(--font-mono)',
              fontSize: 12,
              zIndex: 10,
            }}
          >
            {error}
          </div>
        )}
        {loading && !hasData && !error && (
          <div
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              padding: 16,
              color: 'var(--text-muted)',
              fontFamily: 'var(--font-mono)',
              fontSize: 12,
              zIndex: 10,
            }}
          >
            Loading network…
          </div>
        )}
        {!loading && (
          <div
            style={{
              position: 'absolute',
              top: 16,
              left: 16,
              display: 'flex',
              gap: 16,
              alignItems: 'center',
              zIndex: 10,
            }}
          >
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 600,
              letterSpacing: 1,
              color: 'var(--accent)',
            }}
          >
            {plants?.length ?? 0} PLANTS (ENTERPRISE)
          </span>
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 500,
              letterSpacing: 1,
              color: 'var(--text-muted)',
            }}
          >
            {plantMarkers.length} with lat/lon on map
          </span>
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 500,
              letterSpacing: 1,
              color: 'var(--text-muted)',
            }}
          >
            {supplierMarkers.length} SUPPLIERS (geo)
          </span>
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontSize: 9,
              fontWeight: 500,
              letterSpacing: 1,
              color: 'var(--text-muted)',
            }}
          >
            {links.length} connections
          </span>
        </div>
        )}
      </div>
    </div>
  );
}
