import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Dev convenience: proxy to the mock robot (npm run mock-robot, port 8766) so
    // you can also connect the app to its own origin (enter "localhost:5173" as
    // host) — useful when the browser can't reach other ports directly.
    proxy: {
      '/api': { target: 'http://localhost:8766', changeOrigin: true },
      '/ws': { target: 'ws://localhost:8766', ws: true },
    },
  },
})
