import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// Proxies: UI talks to enterpriseservice (8085) and probability-service (8097).
// Longer paths must be listed first (Vite matches in order).
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/ws': {
        target: 'http://localhost:8097',
        changeOrigin: true,
        ws: true,
      },
      '/api/probability': {
        target: 'http://localhost:8097',
        changeOrigin: true,
      },
      '/api/v1': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
    },
  },
})
