import { createTestingPinia } from "@pinia/testing";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useCourseStore } from "~/store/course";
import { play, playSequence, updateSource } from "../audio";
import {
  playEnglish,
  splitTechnicalTerm,
  useCurrentStatementEnglishSound,
} from "../index";

vi.mock("../audio.ts", () => {
  return {
    updateSource: vi.fn(),
    play: vi.fn(),
    playSequence: vi.fn(),
  };
});

describe("splitTechnicalTerm", () => {
  it.each([
    ["@RestController", ["Rest", "Controller"]],
    ["URLParser", ["URL", "Parser"]],
    ["SqlSessionFactory", ["SQL", "Session", "Factory"]],
    ["controller", ["controller"]],
  ])("splits %s into pronounceable words", (term, expected) => {
    expect(splitTechnicalTerm(term)).toEqual(expected);
  });
});

describe("useCurrentStatementEnglishSound", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    createTestingPinia({
      createSpy: vi.fn,
    });

    const courseStore = useCourseStore();
    courseStore.currentStatement = {
      id: "1",
      order: 1,
      english: "I",
      soundmark: "/I/",
      chinese: "我",
      isMastered: false,
    };

    vi.clearAllMocks();
  });

  it("plays sound", async () => {
    const { playSound } = useCurrentStatementEnglishSound();

    playSound();

    expect(play).toHaveBeenCalled();
  });

  it("should updates audio source", async () => {
    useCurrentStatementEnglishSound();

    // update english value
    const courseStore = useCourseStore();
    courseStore.currentStatement = {
      id: "2",
      order: 2,
      english: "like",
      soundmark: "/like/",
      chinese: "喜欢",
      isMastered: false,
    };
    await vi.advanceTimersToNextTimerAsync();

    expect(updateSource).toBeCalledTimes(1);
  });

  it("does not update audio source if the word is the same", async () => {
    useCurrentStatementEnglishSound();

    const courseStore = useCourseStore();
    courseStore.currentStatement = {
      id: "1",
      order: 1,
      english: "I",
      soundmark: "/I/",
      chinese: "我",
      isMastered: false,
    };

    expect(updateSource).toHaveBeenCalledTimes(1);
  });

  it("plays a compound technical term as sequential dictionary words", async () => {
    const { playSound } = useCurrentStatementEnglishSound();
    const courseStore = useCourseStore();
    courseStore.currentStatement = {
      id: "2",
      order: 2,
      english: "RestController",
      soundmark: "",
      chinese: "REST 控制器",
      isMastered: false,
    };
    await vi.advanceTimersToNextTimerAsync();

    playSound();

    expect(playSequence).toHaveBeenCalledWith([
      "https://dict.youdao.com/dictvoice?type=2&audio=Rest",
      "https://dict.youdao.com/dictvoice?type=2&audio=Controller",
    ], undefined);
    expect(play).not.toHaveBeenCalled();
  });

  it("also splits a technical term played from the related-word list", () => {
    playEnglish("RequestBody");

    expect(playSequence).toHaveBeenCalledWith([
      "https://dict.youdao.com/dictvoice?type=2&audio=Request",
      "https://dict.youdao.com/dictvoice?type=2&audio=Body",
    ], undefined);
  });
});
