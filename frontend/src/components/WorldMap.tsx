import { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap, ZoomControl } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const plants = [
  { name: 'Frankfurt', lat: 50.11, lng: 8.68, type: 'plant' },
  { name: 'Shanghai', lat: 31.23, lng: 121.47, type: 'plant' },
  { name: 'Houston', lat: 29.76, lng: -95.37, type: 'plant' },
  { name: 'Mumbai', lat: 19.08, lng: 72.88, type: 'plant' },
  { name: 'São Paulo', lat: -23.55, lng: -46.63, type: 'plant' },
];

const chokepoints = [
  { name: 'Strait of Hormuz', lat: 26.56, lng: 56.25 },
  { name: 'Strait of Malacca', lat: 2.5, lng: 101.2 },
  { name: 'Suez Canal', lat: 30.42, lng: 32.35 },
  { name: 'Panama Canal', lat: 9.1, lng: -79.68 },
  { name: 'Strait of Gibraltar', lat: 35.97, lng: -5.6 },
  { name: 'Bab el-Mandeb', lat: 12.58, lng: 43.45 },
  { name: 'Danish Straits', lat: 55.8, lng: 10.5 },
  { name: 'Bosphorus', lat: 41.12, lng: 29.05 },
  { name: 'Lombok Strait', lat: -8.5, lng: 115.75 },
  { name: 'Cape of Good Hope', lat: -34.35, lng: 18.47 },
];

const accentIcon = L.divIcon({
  className: 'custom-marker',
  html: '<span style="width:12px;height:12px;border-radius:50%;background:#22D3EE;display:block;border:2px solid #0A0F1C;"></span>',
  iconSize: [12, 12],
  iconAnchor: [6, 6],
});

const warningIcon = L.divIcon({
  className: 'custom-marker',
  html: '<span style="width:8px;height:8px;border-radius:50%;background:#FACC15;display:block;border:1px solid #0A0F1C;"></span>',
  iconSize: [8, 8],
  iconAnchor: [4, 4],
});

function MapBounds() {
  const map = useMap();
  useEffect(() => {
    const allPoints = [...plants, ...chokepoints];
    const bounds = L.latLngBounds(
      allPoints.map((p) => [p.lat, p.lng] as L.LatLngTuple)
    );
    map.fitBounds(bounds, { padding: [40, 40], maxZoom: 4 });
  }, [map]);
  return null;
}

export function WorldMap() {
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
          PREDICTION SIMULATION MAP
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
              Chokepoints
            </span>
          </div>
          <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
            <span
              style={{
                width: 12,
                height: 1,
                background: 'var(--text-tertiary)',
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
              Routes
            </span>
          </div>
        </div>
      </div>

      <div
        style={{
          flex: 1,
          background: 'var(--bg-surface)',
          minHeight: 300,
        }}
      >
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
          <MapBounds />
          {plants.map((p) => (
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
          {chokepoints.map((p) => (
            <Marker key={p.name} position={[p.lat, p.lng]} icon={warningIcon}>
              <Popup>
                <div
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 11,
                    color: 'var(--text-primary)',
                  }}
                >
                  <strong>{p.name}</strong> — Chokepoint
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
          {plants.length + chokepoints.length} NODES
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
          847 CONNECTIONS
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
          LATENCY: 42ms
        </span>
      </div>
    </div>
  );
}
