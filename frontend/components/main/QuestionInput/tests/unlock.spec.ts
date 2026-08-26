import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWrapperQuestionInput } from "../useWrapperQuestionInput";

const mocks = vi.hoisted(() => ({
  unlockCurrentStatement: vi.fn(),
  showAnswer: vi.fn(),
}));

vi.mock("~/store/course", () => ({
  useCourseStore: () => ({
    currentStatement: { id: "statement-1", english: "controller" },
    statementIndex: 0,
    unlockCurrentStatement: mocks.unlockCurrentStatement,
    isAllDone: () => false,
    toNextStatement: vi.fn(),
  }),
}));

vi.mock("~/composables/main/question", () => ({
  useInput: () => ({
    initialize: vi.fn(),
    findWordById: vi.fn(),
    inputValue: { value: "" },
    submitAnswer: (correctCallback?: () => void) => correctCallback?.(),
    setInputValue: vi.fn(),
    handleKeyboardInput: vi.fn(),
    isFixMode: () => false,
    isFixInputMode: () => false,
  }),
}));

vi.mock("~/composables/courses/courseTimer", () => ({
  courseTimer: { timeEnd: vi.fn() },
}));

vi.mock("~/composables/main/game", () => ({
  useGameMode: () => ({ showAnswer: mocks.showAnswer }),
}));

vi.mock("~/composables/main/summary", () => ({
  useSummary: () => ({ showSummary: vi.fn() }),
}));

vi.mock("~/composables/user/autoNext", () => ({
  useAutoNextQuestion: () => ({ isAutoNextQuestion: () => false }),
}));

vi.mock("~/composables/user/sound", () => ({
  useKeyboardSound: () => ({ isKeyboardSoundEnabled: () => false }),
}));

vi.mock("~/composables/user/submitKey", () => ({
  useSpaceSubmitAnswer: () => ({ isUseSpaceSubmitAnswer: () => false }),
}));

vi.mock("../questionInputHelper", () => ({
  useQuestionInput: () => ({
    setInputCursorPosition: vi.fn(),
    getInputCursorPosition: vi.fn(),
    blurInput: vi.fn(),
    focusInput: vi.fn(),
  }),
}));

vi.mock("../useAnswerError", () => ({
  useAnswerError: () => ({ handleAnswerError: vi.fn() }),
}));

vi.mock("../useTypingSound", () => ({
  useTypingSound: () => ({ checkPlayTypingSound: () => false, playTypingSound: vi.fn() }),
  usePlayTipSound: () => ({ playRightSound: vi.fn() }),
}));

describe("useWrapperQuestionInput word unlock", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("unlocks the current related word after a correct submission", () => {
    const questionInput = useWrapperQuestionInput();

    questionInput.submitAnswer();

    expect(mocks.unlockCurrentStatement).toHaveBeenCalledOnce();
    expect(mocks.showAnswer).toHaveBeenCalledOnce();
  });
});
