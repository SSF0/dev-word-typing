import { defineVitestConfig } from "@nuxt/test-utils/config";
import { configDefaults } from "vitest/config";

export default defineVitestConfig({
  test: {
    environment: "happy-dom",
    exclude: [
      ...configDefaults.exclude,
      "tests/config/nuxt-dependency-baseline.test.ts",
    ],
  },
});
