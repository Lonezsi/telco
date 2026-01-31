import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/products': {
        target: process.env.DOCKER_RUN ? 'http://backend:8080' : 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})