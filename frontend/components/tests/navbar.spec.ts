import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

const navbarSource = readFileSync(resolve(process.cwd(), "components/Navbar.vue"), "utf8");

describe("Navbar on the game page", () => {
  it("keeps only the home link and practice actions in the topmost row", () => {
    expect(navbarSource).toContain('data-test="game-navbar"');
    expect(navbarSource).toContain('data-test="home-link"');
    expect(navbarSource).toContain('data-test="detail-toggle"');
    expect(navbarSource).toContain('data-test="game-settings"');
    expect(navbarSource).toContain('data-test="pause-game"');
    expect(navbarSource).toContain('data-test="reset-course"');
    expect(navbarSource).toContain('@click="toggleDetail"');
    expect(navbarSource).toContain('@click="openGameSettingModal"');
    expect(navbarSource).toContain('@click="pauseGame"');
    expect(navbarSource).toContain('@click="handleDoAgain"');
  });

  it("keeps the brand and dark-mode control out of the game-only branch", () => {
    expect(navbarSource).toContain('<template v-if="isGameRoute">');
    expect(navbarSource).toContain('<template v-else>');
    expect(navbarSource).toContain('data-test="brand-title"');
    expect(navbarSource).toContain('data-test="dark-mode"');
  });

  it("uses a full-width in-flow divider on the game page", () => {
    expect(navbarSource).toContain("'shrink-0 border-b border-gray-200 dark:border-gray-700': isGameRoute");
    expect(navbarSource).toContain(
      'const isStickyNavBar = computed(() => route.name === "course-pack-id");',
    );
    expect(navbarSource).not.toContain(
      'class="flex h-12 items-center justify-between border-b border-gray-200 dark:border-gray-700"',
    );
  });
});
