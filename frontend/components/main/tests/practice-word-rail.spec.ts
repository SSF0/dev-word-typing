import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import PracticeWordRail from "../PracticeWordRail.vue";

const mocks = vi.hoisted(() => {
  const unlockedStatementIds = new Set<string>();
  const statements = [
    {
      id: "statement-1",
      prefix: "@",
      english: "controller",
      chinese: "控制器；接收请求并协调处理",
    },
    {
      id: "statement-2",
      english: "mapping",
      chinese: "映射；把请求路径绑定到处理方法",
    },
    {
      id: "statement-3",
      english: "handler",
      chinese: "处理器；实际处理请求的方法",
    },
  ];

  return {
    unlockedStatementIds,
    courseStore: {
      currentCourse: {
        id: "course-1",
        statements,
      },
      currentStatement: statements[0],
      isStatementUnlocked: (statementId: string) => unlockedStatementIds.has(statementId),
    },
  };
});

vi.mock("~/store/course", () => ({
  useCourseStore: () => mocks.courseStore,
}));

describe("PracticeWordRail", () => {
  beforeEach(() => {
    mocks.unlockedStatementIds.clear();
  });

  it("keeps related words in a locked single column beside practice", () => {
    const wrapper = mount(PracticeWordRail);
    const rail = wrapper.get('[data-test="practice-word-rail"]');
    const keywords = wrapper.findAll('[data-test="learning-keyword"]');

    expect(rail.classes()).toContain("practice-word-rail");
    expect(rail.text()).toContain("本节内容");
    expect(rail.text()).not.toContain("相关词");
    expect(keywords).toHaveLength(3);
    expect(keywords[0].attributes("data-active")).toBe("true");
    expect(keywords[0].attributes("data-revealed")).toBe("false");
    expect(keywords[0].get('[data-test="keyword-content"]').classes()).toContain("is-blurred");
    expect(keywords[0].text()).toContain("点击查看");
  });

  it("uses an inward right divider and keeps padding inside every word", () => {
    const wrapper = mount(PracticeWordRail);
    const rail = wrapper.get('[data-test="practice-word-rail"]');
    const keywords = wrapper.findAll('[data-test="learning-keyword"]');

    expect(rail.classes()).toEqual(expect.arrayContaining(["border-r", "pr-3"]));
    expect(rail.classes()).not.toContain("border-l");
    expect(keywords.every((keyword) => keyword.classes().includes("px-3"))).toBe(true);
  });

  it("keeps the rail full height without stretching individual words", () => {
    const wrapper = mount(PracticeWordRail);
    const rail = wrapper.get('[data-test="practice-word-rail"]');
    const list = wrapper.get(".word-list");
    const keywords = wrapper.findAll('[data-test="learning-keyword"]');

    expect(rail.classes()).toEqual(
      expect.arrayContaining(["flex", "h-full", "min-h-0", "flex-col"]),
    );
    expect(list.classes()).toEqual(
      expect.arrayContaining(["flex", "min-h-0", "flex-1", "flex-col"]),
    );
    expect(
      keywords.every((keyword) =>
        ["flex", "h-16", "shrink-0", "items-center"].every((name) =>
          keyword.classes().includes(name),
        ),
      ),
    ).toBe(true);
    expect(keywords.every((keyword) => !keyword.classes().includes("flex-1"))).toBe(true);
  });

  it("reveals only the selected word when it is clicked", async () => {
    const wrapper = mount(PracticeWordRail);
    const keywords = wrapper.findAll('[data-test="learning-keyword"]');

    await keywords[1].trigger("click");

    expect(keywords[0].attributes("data-revealed")).toBe("false");
    expect(keywords[1].attributes("data-revealed")).toBe("true");
    expect(keywords[1].text()).toContain("mapping");
  });

  it("reveals a word automatically after practice unlocks it", () => {
    mocks.unlockedStatementIds.add("statement-1");

    const wrapper = mount(PracticeWordRail);
    const currentKeyword = wrapper.findAll('[data-test="learning-keyword"]')[0];

    expect(currentKeyword.attributes("data-revealed")).toBe("true");
    expect(currentKeyword.text()).toContain("已解锁");
    expect(currentKeyword.text()).toContain("@controller");
    expect(currentKeyword.get('[data-test="keyword-content"]').classes()).not.toContain(
      "is-blurred",
    );
  });
});
