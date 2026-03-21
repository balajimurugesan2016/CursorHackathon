import { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap, ZoomControl } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useAsync } from '../hooks/useAsync';
import { enterpriseApi } from '../api/client';
import { parseCoord } from '../util/geo';

const accentIcon = L.divIcon({
  className: 'custom-marker',
  html: '<span style="width:12px;height:12px;border-radius:50%;background:#22D3EE;display:block;border:2px solid #0A0F1C;"></span>',
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

interface WorldMapProps {
  title?: string;
}

export function WorldMap({ title = 'PREDICTION SIMULATION MAP' }: WorldMapProps) {
  const { data: plants, loading, error } = useAsync(() => enterpriseApi.listPlants(), []);

  const markers =
    plants?.flatMap((p) => {
      const lat = parseCoord(p.latitude);
      const lon = parseCoord(p.longitude);
      if (lat == null || lon == null) return [];
      return [{ name: p.plantName, lat, lng: lon }];
    }) ?? [];

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
          {title}
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
              Plants (enterprise)
            </span>
          </div>
        </div>
      </div>

      {error && (
        <div style={{ padding: 12, color: 'var(--error)', fontFamily: 'var(--font-mono)', fontSize: 11 }}>
          {error}
        </div>
      )}

      <div
        style={{
          flex: 1,
          background: 'var(--bg-surface)',
          minHeight: 300,
        }}
      >
        {loading && markers.length === 0 && !error && (
          <div style={{ padding: 16, color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>
            Loading map…
          </div>
        )}
        <MapContainer
          center={[20, 20]}
          zoom={2}
          style={{ height: '100%', width: '100%' }}
          zoomControl={false}
        >
          <ZoomControl position="bottomright" />
          <TileLayer
            attribution='&copy; <a href="https://carto.com/">CARTO</a>'
            url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
          />
          <MapBounds points={markers.map((m) => ({ lat: m.lat, lng: m.lng }))} />
          {markers.map((p) => (
            <Marker key={p.name} position={[p.lat, p.lng]} icon={accentIcon}>
              <Popup>
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 11,
                    color: 'var(--text-primary)',
                  }}
                >
                  <strong>{p.name}</strong> — Plant
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>

      <div
        style={{
          padding: '8px 16px',
          borderTop: '1px solid var(--border)',
          display: 'flex',
          gap: 16,
          alignItems: 'center',
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
          {markers.length} with coordinates on map
        </span>
      </div>
    </div>
  );
}
