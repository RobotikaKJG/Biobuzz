import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Local API: WiFi connect/disconnect SSE (server/index.mjs on 5174)
      '/api/live-connect': { target: 'http://127.0.0.1:5174', changeOrigin: true },
      '/api/live-disconnect': { target: 'http://127.0.0.1:5174', changeOrigin: true },
      '/api/net-status': { target: 'http://127.0.0.1:5174', changeOrigin: true },
      '/api/health': { target: 'http://127.0.0.1:5174', changeOrigin: true },
      // Dev convenience: mock robot on 8766 (npm run mock-robot)
      '/api': { target: 'http://localhost:8766', changeOrigin: true },
      '/ws': { target: 'ws://localhost:8766', ws: true },
    },
  },
})
