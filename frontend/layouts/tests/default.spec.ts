import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

const layoutSource = readFileSync(resolve(process.cwd(), "layouts/default.vue"), "utf8");
const gamePageSource = readFileSync(
  resolve(process.cwd(), "pages/game/[coursePackId]/[id].vue"),
  "utf8",
);

describe("game page viewport layout", () => {
  it("keeps the game inside one viewport without a page scrollbar", () => {
    expect(layoutSource).toContain("'h-dvh overflow-hidden': isGameRoute");
    expect(layoutSource).toContain("'min-h-0 overflow-hidden': isGameRoute");
    expect(gamePageSource).toMatch(/class="[^"]*h-full[^"]*min-h-0/);
  });

  it("does not clip the wide desktop workspace at the game page boundary", () => {
    const gamePageRootClass = gamePageSource.match(/<div class="([^"]+)"/)?.[1];

    expect(gamePageRootClass).toBeDefined();
    expect(gamePageRootClass).not.toContain("overflow-hidden");
  });

  it("lets the wide desktop workspace extend past the centered content wrapper", () => {
    expect(layoutSource).toMatch(
      /class="mx-auto flex w-full max-w-screen-xl flex-1"\s*:class="\{ 'min-h-0': isGameRoute \}"/,
    );
  });

  it("omits the global copyright footer on the game page", () => {
    expect(layoutSource).toContain('<Footer v-if="!isGameRoute"');
    expect(layoutSource).toContain(
      'const isGameRoute = computed(() => route.name === "game-coursePackId-id");',
    );
  });
});
