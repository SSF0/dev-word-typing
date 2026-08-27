import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    environment: "node",
    include: ["tests/config/nuxt-dependency-baseline.test.ts"],
  },
});
