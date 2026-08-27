import vue from "@vitejs/plugin-vue";
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";

const frontendRoot = fileURLToPath(new URL(".", import.meta.url));

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "~": frontendRoot,
      "@": frontendRoot,
    },
  },
  server: {
    port: 3002,
  },
  preview: {
    port: 3003,
  },
});
