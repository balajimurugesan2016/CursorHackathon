import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// Proxies: UI talks only to enterpriseservice (8085) + simulation agent / supply-chain-risk (8094).
// Longer paths must be listed first (Vite matches in order).
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/agent/supply-chain-risk-report': {
        target: 'http://localhost:8094',
        changeOrigin: true,
      },
      '/api/v1': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
    },
  },
})
