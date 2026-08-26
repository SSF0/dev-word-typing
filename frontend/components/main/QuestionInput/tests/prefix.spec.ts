import { mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { ref } from "vue";
import { describe, expect, it, vi } from "vitest";

import QuestionInput from "../QuestionInput.vue";

vi.mock("~/store/course", () => ({
  useCourseStore: () => ({
    words: ["RestController"],
    currentStatement: { prefix: "@", english: "RestController" },
    statementIndex: 0,
  }),
}));

vi.mock("../questionInputHelper", () => ({
  useQuestionInput: () => ({
    inputEl: ref<HTMLInputElement>(),
    focusing: ref(true),
    focusInput: vi.fn(),
    blurInput: vi.fn(),
  }),
  getWordWidth: () => 15,
}));

vi.mock("../useWrapperQuestionInput", () => ({
  useWrapperQuestionInput: () => ({
    initializeQuestionInput: vi.fn(),
    findWordById: () => ({ userInput: "Rest", isActive: true, incorrect: false }),
    isFixMode: () => false,
    inputValue: ref("Rest"),
    submitAnswer: vi.fn(),
    handleKeyboardInput: vi.fn(),
    setInputValue: vi.fn(),
  }),
}));

vi.mock("~/composables/main/question", () => ({ isWord: () => true }));
vi.mock("~/composables/user/words", () => ({
  useShowWordsWidth: () => ({ isShowWordsWidth: () => true }),
}));
vi.mock("~/composables/main/answerTip", () => ({
  useAnswerTip: () => ({ toggleAnswerTip: vi.fn(), isAnswerTip: () => false }),
}));
vi.mock("../useAnswerError", () => ({
  useAnswerError: () => ({ resetCloseTip: vi.fn() }),
}));
vi.mock("~/composables/main/englishSound", () => ({
  useCurrentStatementEnglishSound: () => ({ playSound: vi.fn() }),
}));
vi.mock("~/composables/courses/courseTimer", () => ({
  courseTimer: { time: vi.fn() },
}));
vi.mock("~/utils/platform", () => ({ isWindows: () => false }));

describe("QuestionInput prefix", () => {
  it("keeps the configured prefix visible without including it in the editable answer", () => {
    const wrapper = mount(QuestionInput, {
      global: {
        plugins: [createPinia()],
        stubs: { MainMasteredBtn: true },
      },
    });

    expect(wrapper.get('[data-test="statement-prefix"]').text()).toBe("@");
    expect(wrapper.text()).toContain("Rest");
    expect((wrapper.get("input").element as HTMLInputElement).value).toBe("Rest");
  });
});
