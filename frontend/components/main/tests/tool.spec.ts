import { shallowMount } from "@vue/test-utils";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, expect, it, vi } from "vitest";

import Tool from "../Tool.vue";

const mocks = vi.hoisted(() => ({
  openGameSettingModal: vi.fn(),
  pauseGame: vi.fn(),
  openCourseContents: vi.fn(),
  modalOpen: vi.fn(),
}));

mockNuxtImport("useModal", () => () => ({ open: mocks.modalOpen }));

vi.mock("~/components/main/QuestionInput/questionInputHelper", () => ({
  useQuestionInput: () => ({ focusInput: vi.fn() }),
}));

vi.mock("~/composables/courses/courseTimer", () => ({
  courseTimer: { reset: vi.fn() },
}));

vi.mock("~/composables/main/game", () => ({
  useGameMode: () => ({ showQuestion: vi.fn() }),
}));

vi.mock("~/composables/main/question", () => ({
  clearQuestionInput: vi.fn(),
}));

vi.mock("~/composables/main/useCourseContents", () => ({
  useCourseContents: () => ({ openCourseContents: mocks.openCourseContents }),
}));

vi.mock("~/composables/main/useGamePause", () => ({
  useGamePause: () => ({ pauseGame: mocks.pauseGame }),
}));

vi.mock("~/composables/main/useGameSetting", () => ({
  useGameSetting: () => ({ openGameSettingModal: mocks.openGameSettingModal }),
}));

vi.mock("~/composables/user/gamePlayMode", () => ({
  useGamePlayMode: () => ({ isDictationMode: () => false }),
}));

vi.mock("~/composables/user/shortcutKey", () => ({
  parseShortcut: () => [],
  useShortcutKeyMode: () => ({ shortcutKeys: { value: { pause: "Ctrl+p" } } }),
}));

vi.mock("~/services/auth", () => ({
  isAuthenticated: () => false,
}));

vi.mock("~/store/course", () => ({
  useCourseStore: () => ({
    currentCourse: { id: "course-1", coursePackId: "pack-1", title: "Java 常用注解" },
    visibleStatementIndex: 2,
    visibleStatementsCount: 18,
    isAllDone: () => false,
    doAgain: vi.fn(),
  }),
}));

describe("Tool", () => {
  it("keeps course information and progress inside the practice pane", () => {
    const wrapper = shallowMount(Tool, {
      global: {
        renderStubDefaultSlot: true,
        stubs: {
          NuxtLink: { template: "<a><slot /></a>" },
        },
      },
    });

    expect(wrapper.text()).toContain("Java 常用注解（3/18）");
    expect(wrapper.find('[data-test="game-settings"]').exists()).toBe(false);
    expect(wrapper.find('[data-test="pause-game"]').exists()).toBe(false);
    expect(wrapper.find('[data-test="reset-course"]').exists()).toBe(false);
    const heading = wrapper.get("div");
    expect(heading.classes()).toEqual(expect.arrayContaining(["pt-4", "text-base"]));
    expect(heading.classes()).not.toContain("border-t");
  });
});
