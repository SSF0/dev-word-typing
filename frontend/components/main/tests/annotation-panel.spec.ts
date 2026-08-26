import { flushPromises, mount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";

import AnnotationPanel from "../AnnotationPanel.vue";

const mocks = vi.hoisted(() => {
  return {
    courseStore: {
      currentCourse: {
        id: "course-1",
        coursePackId: "pack-1",
        title: "Java 常用注解",
        practiceType: "WORD",
      },
      currentStatement: {
        id: "statement-1",
        prefix: "@",
        english: "RestController",
        chinese: "REST 控制器；接收请求并返回 JSON",
        explanation:
          "## 核心作用\n@RestController 让方法返回值直接写入响应体。\n\n## 使用要点\nController 只调用 Service 并组织响应。",
        usageExample:
          "```java\n@RestController // 声明 REST 控制器\npublic class UserController { } // 定义用户控制器\n```",
        referenceCode: "@Controller\n@ResponseBody",
        note: "当前注解笔记",
      },
    },
    updateStatementNote: vi.fn(),
  };
});

vi.mock("~/store/course", () => ({
  useCourseStore: () => mocks.courseStore,
}));

vi.mock("~/api/course", () => ({
  updateStatementNote: mocks.updateStatementNote,
}));

vi.mock("vue-sonner", () => ({
  toast: { success: vi.fn(), error: vi.fn() },
}));

describe("AnnotationPanel", () => {
  it("shows details for the current learning item instead of the whole section", () => {
    const wrapper = mount(AnnotationPanel);

    expect(wrapper.get('[data-test="detail-term"]').text()).toContain("@RestController");
    const sections = wrapper.findAll('[data-test="learning-section"]');
    expect(sections).toHaveLength(2);
    expect(sections[0].text()).toContain("核心作用");
    expect(sections[0].text()).toContain("直接写入响应体");
    expect(sections[1].text()).toContain("使用要点");
    expect(wrapper.find('[data-test="learning-keywords"]').exists()).toBe(false);
  });

  it("collapses usage and source details until the learner asks for them", () => {
    const wrapper = mount(AnnotationPanel);
    const usage = wrapper.get('[data-test="usage-example"]');
    const source = wrapper.get('[data-test="annotation-source"]');

    expect(usage.element.tagName).toBe("DETAILS");
    expect(usage.attributes("open")).toBeUndefined();
    expect(usage.text()).toContain("public class UserController");
    expect(source.element.tagName).toBe("DETAILS");
    expect(source.attributes("open")).toBeUndefined();
    expect(source.text()).toContain("@Controller");
    expect(source.text()).toContain("@ResponseBody");
  });

  it("uses the 使用示例 label without exposing a related project location", () => {
    const wrapper = mount(AnnotationPanel);

    const usage = wrapper.get('[data-test="usage-example"]');
    expect(usage.text()).toContain("使用示例");
    expect(usage.text()).not.toContain("使用实例");
    expect(usage.text()).toContain("public class UserController");
    expect(wrapper.find('[data-test="project-path"]').exists()).toBe(false);
  });

  it("renders a fenced Markdown example as commented source code", async () => {
    const wrapper = mount(AnnotationPanel);
    const usage = wrapper.get('[data-test="usage-example"]');

    expect(usage.text()).not.toContain("```java");
    const code = usage.get('code[data-language="java"]');
    expect(code.classes()).toContain("language-java");
    expect(code.text()).toContain("@RestController // 声明 REST 控制器");
    expect(code.text()).toContain("public class UserController { } // 定义用户控制器");
  });

  it("lets the note editor and save action fill the remaining detail space", () => {
    const wrapper = mount(AnnotationPanel);
    const body = wrapper.get(".annotation-body");
    const note = wrapper.get(".note-section");
    const textarea = note.get("textarea");
    const saveButton = note.get("button");

    expect(body.element.lastElementChild).toBe(note.element);
    expect(note.classes()).toEqual(
      expect.arrayContaining(["flex", "min-h-0", "flex-1", "flex-col"]),
    );
    expect(textarea.classes()).toEqual(
      expect.arrayContaining(["min-h-28", "flex-1", "resize-none"]),
    );
    expect(saveButton.classes()).toContain("w-full");
    expect((textarea.element as HTMLTextAreaElement).value).toBe("当前注解笔记");
  });

  it("saves a note on the current learning item", async () => {
    mocks.updateStatementNote.mockResolvedValue({
      ...mocks.courseStore.currentStatement,
      note: "新的理解",
    });
    const wrapper = mount(AnnotationPanel);

    await wrapper.get("textarea").setValue("新的理解");
    await wrapper.get("button.btn-primary").trigger("click");
    await flushPromises();

    expect(mocks.updateStatementNote).toHaveBeenCalledWith(
      "pack-1",
      "course-1",
      "statement-1",
      "新的理解",
    );
  });
});
