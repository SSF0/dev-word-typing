import { flushPromises, mount } from "@vue/test-utils";
import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { beforeEach, describe, expect, it, vi } from "vitest";

import GamePage from "../[coursePackId]/[id].vue";

const workspaceSource = readFileSync(resolve(process.cwd(), "components/main/PracticeWorkspace.vue"), "utf8");

const mocks = vi.hoisted(() => ({
  toggleGamePlayMode: vi.fn(),
}));

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { coursePackId: "pack-1", id: "course-1" } }),
}));

vi.mock("vue-sonner", () => ({
  toast: { info: vi.fn() },
}));

vi.mock("~/composables/main/game", () => ({
  useGameMode: () => ({ showQuestion: vi.fn() }),
}));

vi.mock("~/composables/useNavigation", () => ({
  useNavigation: () => ({ gotoCourseList: vi.fn() }),
}));

vi.mock("~/composables/user/gamePlayMode", () => ({
  GamePlayMode: { ChineseToEnglish: "CHINESE_TO_ENGLISH" },
  useGamePlayMode: () => ({ toggleGamePlayMode: mocks.toggleGamePlayMode }),
}));

vi.mock("~/services/auth", () => ({
  isAuthenticated: () => false,
}));

vi.mock("~/store/course", () => ({
  useCourseStore: () => ({
    currentCourse: { practiceType: "WORD" },
    isAllMastered: () => false,
    setup: vi.fn().mockResolvedValue(undefined),
  }),
}));

vi.mock("~/store/coursePack", () => ({
  useCoursePackStore: () => ({
    setupCoursePack: vi.fn().mockResolvedValue(undefined),
  }),
}));

vi.mock("~/store/masteredElements", () => ({
  useMasteredElementsStore: () => ({ setup: vi.fn().mockResolvedValue(undefined) }),
}));

const MainToolStub = {
  template: '<div data-test="tool"><slot name="actions" /></div>',
};

const MainAnnotationPanelStub = {
  emits: ["close"],
  template: '<button data-test="panel-close" @click="$emit(\'close\')">关闭</button>',
};

const MainPracticeWordRailStub = {
  template: '<aside data-test="practice-word-rail" />',
};

async function mountPage() {
  const wrapper = mount(GamePage, {
    global: {
      stubs: {
        Loading: { template: '<div data-test="loading" />' },
        MainTool: MainToolStub,
        Tool: MainToolStub,
        MainGame: {
          template:
            '<div data-test="game-mode" /><div data-test="game-shortcuts" />',
        },
        Game: {
          template:
            '<div data-test="game-mode" /><div data-test="game-shortcuts" />',
        },
        MainAnnotationPanel: MainAnnotationPanelStub,
        AnnotationPanel: MainAnnotationPanelStub,
        MainPracticeWordRail: MainPracticeWordRailStub,
        PracticeWordRail: MainPracticeWordRailStub,
      },
    },
  });

  await flushPromises();
  return wrapper;
}

describe("practice workspace detail panel", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("starts centered with detail controls in the closed state", async () => {
    const wrapper = await mountPage();

    expect(wrapper.get('[data-test="practice-workspace"]').attributes("data-state")).toBe("closed");
    expect(wrapper.get('[data-test="detail-toggle"]').attributes("aria-expanded")).toBe("false");
    expect(wrapper.get('[data-test="detail-panel"]').attributes("aria-hidden")).toBe("true");
    expect(wrapper.find('[data-test="detail-backdrop"]').exists()).toBe(false);
    expect(wrapper.find('[data-test="practice-word-rail"]').exists()).toBe(true);
  });

  it("keeps the related-word rail immediately to the left of practice", async () => {
    const wrapper = await mountPage();
    const pane = wrapper.get(".practice-pane");
    const rail = wrapper.get('[data-test="practice-word-rail"]');

    expect(rail.element.nextElementSibling).toBe(pane.element);

    await wrapper.get('[data-test="detail-toggle"]').trigger("click");

    expect(rail.element.nextElementSibling).toBe(pane.element);
    expect(pane.element.nextElementSibling).toBe(wrapper.get('[data-test="detail-panel"]').element);
  });

  it("keeps the game mode and shortcuts in the original flex height flow", async () => {
    const wrapper = await mountPage();
    const workspace = wrapper.get('[data-test="practice-workspace"]');
    const track = wrapper.get(".practice-track");
    const pane = wrapper.get(".practice-pane");
    const gameMode = wrapper.get('[data-test="game-mode"]');
    const gameShortcuts = wrapper.get('[data-test="game-shortcuts"]');

    expect(workspace.classes()).toEqual(expect.arrayContaining(["flex", "min-h-0", "flex-1"]));
    expect(track.classes()).toEqual(expect.arrayContaining(["min-h-0", "flex-1"]));
    expect(pane.classes()).toEqual(
      expect.arrayContaining(["flex", "h-full", "min-h-0", "flex-col"]),
    );
    expect(gameMode.element.parentElement).toBe(pane.element);
    expect(gameShortcuts.element.parentElement).toBe(pane.element);
  });

  it("centers the visible two-column and three-column groups without shrinking practice", () => {
    expect(workspaceSource).toContain("--practice-width: 56rem");
    expect(workspaceSource).toContain("--word-rail-width: 12rem");
    expect(workspaceSource).toContain("--detail-width: 32rem");
    expect(workspaceSource).toContain("--closed-workspace-width: 69rem");
    expect(workspaceSource).toContain("--open-workspace-width: 102rem");
    expect(workspaceSource).toMatch(/\.practice-track\s*\{[\s\S]*?margin-inline: auto;/);
    expect(workspaceSource).toContain(
      "grid-template-columns: var(--word-rail-width) var(--practice-width)",
    );
  });

  it("constrains the related-word rail at regular desktop widths so it scrolls locally", () => {
    const baseStyles = workspaceSource.split("@media (min-width: 1280px)")[0];

    expect(baseStyles).toMatch(
      /\.practice-word-column\s*\{[\s\S]*?height: calc\(100dvh - 8rem\);[\s\S]*?max-height: calc\(100dvh - 8rem\);/,
    );
  });

  it("uses a centered inline detail at wide viewports and a drawer below that", () => {
    expect(workspaceSource).toMatch(
      /@media \(min-width: 1700px\)[\s\S]*?\.is-detail-open \.practice-track\s*\{[\s\S]*?width: var\(--open-workspace-width\);/,
    );
    expect(workspaceSource).toMatch(
      /@media \(min-width: 1700px\)[\s\S]*?\.practice-track\s*\{[\s\S]*?margin-inline: 0;/,
    );
    expect(workspaceSource).toMatch(
      /@media \(min-width: 1700px\)[\s\S]*?\.detail-panel\s*\{[\s\S]*?transform: none;/,
    );
    expect(workspaceSource).toMatch(
      /@media \(min-width: 1700px\)[\s\S]*?\.is-detail-open \.detail-panel\s*\{[\s\S]*?height: calc\(100dvh - 8rem\);/,
    );
  });

  it("opens the detail panel from the toolbar action", async () => {
    const wrapper = await mountPage();

    await wrapper.get('[data-test="detail-toggle"]').trigger("click");

    expect(wrapper.get('[data-test="practice-workspace"]').attributes("data-state")).toBe("open");
    expect(wrapper.get('[data-test="detail-toggle"]').attributes("aria-expanded")).toBe("true");
    expect(wrapper.get('[data-test="detail-toggle"]').text()).toContain("收起详情");
    expect(wrapper.get('[data-test="detail-panel"]').attributes("aria-hidden")).toBe("false");
    expect(wrapper.find('[data-test="detail-backdrop"]').exists()).toBe(true);
  });

  it("closes the detail panel from the toolbar action", async () => {
    const wrapper = await mountPage();
    const toggle = wrapper.get('[data-test="detail-toggle"]');

    await toggle.trigger("click");
    await toggle.trigger("click");

    expect(wrapper.get('[data-test="practice-workspace"]').attributes("data-state")).toBe("closed");
    expect(toggle.text()).toContain("查看详情");
  });

  it("closes when the annotation panel requests it", async () => {
    const wrapper = await mountPage();

    await wrapper.get('[data-test="detail-toggle"]').trigger("click");
    await wrapper.get('[data-test="panel-close"]').trigger("click");

    expect(wrapper.get('[data-test="practice-workspace"]').attributes("data-state")).toBe("closed");
  });

  it("closes when Escape is pressed", async () => {
    const wrapper = await mountPage();

    await wrapper.get('[data-test="detail-toggle"]').trigger("click");
    window.dispatchEvent(new KeyboardEvent("keydown", { key: "Escape" }));
    await wrapper.vm.$nextTick();

    expect(wrapper.get('[data-test="practice-workspace"]').attributes("data-state")).toBe("closed");
  });
});
