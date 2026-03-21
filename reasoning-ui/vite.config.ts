import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Longer path first: `for (context in proxies)` matches first prefix (see Vite proxyMiddleware).
      "/api/agent/supply-chain-risk-report": {
        target: "http://localhost:8094",
        changeOrigin: true,
      },
      "/api": {
        target: "http://localhost:8093",
        changeOrigin: true,
      },
    },
  },
});
