import { readFileSync } from "node:fs";

import { describe, expect, it } from "vitest";

type PackageJson = {
  dependencies: Record<string, string>;
  devDependencies: Record<string, string>;
  pnpm?: {
    overrides?: Record<string, string>;
  };
};

const packageJson = JSON.parse(
  readFileSync(new URL("../../package.json", import.meta.url), "utf8"),
) as PackageJson;

describe("Nuxt 3.12 development dependency baseline", () => {
  it.each([
    ["@nuxt/image", "1.7.0"],
    ["@nuxt/kit", "3.12.4"],
    ["@nuxt/schema", "3.12.4"],
    ["@nuxt/test-utils", "3.13.1"],
    ["@nuxt/ui", "2.18.4"],
    ["@pinia/testing", "0.1.3"],
    ["@vue/test-utils", "2.4.6"],
    ["@vueuse/core", "10.11.0"],
    ["@vueuse/nuxt", "10.10.1"],
    ["happy-dom", "13.10.1"],
    ["nitropack", "2.9.6"],
    ["nuxt", "3.12.1"],
    ["tailwindcss", "3.4.4"],
    ["typescript", "5.4.5"],
    ["vite", "5.3.1"],
    ["vitest", "1.6.0"],
    ["vue", "3.4.29"],
    ["vue-router", "4.3.3"],
    ["vue-tsc", "2.0.29"],
  ])("pins %s to %s", (dependency, version) => {
    expect(packageJson.devDependencies[dependency]).toBe(version);
  });

  it("keeps Pinia compatible with Vue 3.4", () => {
    expect(packageJson.dependencies.pinia).toBe("2.1.7");
  });

  it.each([
    ["@headlessui/vue", "1.7.22"],
    ["@nuxt/devtools", "1.3.3"],
    ["@nuxt/devtools-kit", "1.3.9"],
    ["@nuxt/icon", "1.4.5"],
    ["@nuxt/kit", "3.12.4"],
    ["@nuxt/schema", "3.12.4"],
    ["@nuxtjs/color-mode", "3.4.2"],
    ["@nuxtjs/tailwindcss", "6.12.1"],
    ["magicast", "0.3.4"],
    ["nitropack", "2.9.6"],
    ["nuxi", "3.12.0"],
    ["pkg-types", "1.1.3"],
    ["unstorage", "1.10.2"],
    ["vite", "5.3.1"],
    ["vue-router", "4.3.3"],
  ])("overrides %s with %s", (dependency, version) => {
    expect(packageJson.pnpm?.overrides?.[dependency]).toBe(version);
  });
});
