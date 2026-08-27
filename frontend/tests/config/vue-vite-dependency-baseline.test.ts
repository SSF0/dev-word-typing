import { readFileSync } from "node:fs";
import { resolve } from "node:path";

import { describe, expect, it } from "vitest";

type PackageJson = {
  dependencies: Record<string, string>;
  devDependencies: Record<string, string>;
  scripts: Record<string, string>;
};

const packageJson = JSON.parse(
  readFileSync(resolve(process.cwd(), "package.json"), "utf8"),
) as PackageJson;

const allDependencies = {
  ...packageJson.dependencies,
  ...packageJson.devDependencies,
};

describe("Vue 3 + Vite dependency baseline", () => {
  it.each([
    ["@vitejs/plugin-vue", "5.2.4"],
    ["@vue/test-utils", "2.4.6"],
    ["@vueuse/core", "10.11.0"],
    ["autoprefixer", "10.5.4"],
    ["daisyui", "4.12.24"],
    ["happy-dom", "13.10.1"],
    ["postcss", "8.5.26"],
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
    "nuxt",
    "nitropack",
    "@nuxt/ui",
    "@nuxt/image",
    "@nuxt/kit",
    "@nuxt/schema",
    "@nuxt/test-utils",
    "@vueuse/nuxt",
    "@hypernym/nuxt-anime",
  ])("does not depend on %s", (dependency) => {
    expect(allDependencies[dependency]).toBeUndefined();
  });

  it("uses Vite for the default lifecycle scripts", () => {
    expect(packageJson.scripts.dev).toBe("vite --port 3002");
    expect(packageJson.scripts.build).toBe("vite build");
    expect(packageJson.scripts.preview).toBe("vite preview --port 3003");
    expect(packageJson.scripts["type-check"]).toBe("vue-tsc --noEmit -p tsconfig.json");
  });
});
