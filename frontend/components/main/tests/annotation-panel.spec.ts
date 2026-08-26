import { mount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";

import AnnotationPanel from "../AnnotationPanel.vue";

const mocks = vi.hoisted(() => {
  return {
    courseStore: {
      currentCourse: {
        id: "course-1",
        coursePackId: "pack-1",
        title: "@Controller",
        practiceType: "WORD",
        annotationExplain:
          "## 核心作用\n@Controller 把一个类标记为 Spring MVC 控制器。\n\n## 向下展开一层\n@RequestMapping 负责映射，handler method 负责处理。\n\n## 常见用法\n控制器通常调用 Service，返回视图名或响应体。\n\n## 源码拆解\n@Target(TYPE)：只能标在类型上。\n@Retention(RUNTIME)：运行时仍然保留。",
        annotationCode:
          "@Target({ElementType.TYPE})\n@Retention(RetentionPolicy.RUNTIME)\npublic @interface Controller {}",
        note: "",
      },
    },
  };
});

vi.mock("~/store/course", () => ({
  useCourseStore: () => mocks.courseStore,
}));

vi.mock("~/api/course", () => ({
  updateCourseNote: vi.fn(),
}));

vi.mock("vue-sonner", () => ({
  toast: { success: vi.fn(), error: vi.fn() },
}));

describe("AnnotationPanel", () => {
  it("keeps the essential explanation visible and leaves related words outside", () => {
    const wrapper = mount(AnnotationPanel);

    const sections = wrapper.findAll('[data-test="learning-section"]');
    expect(sections).toHaveLength(2);
    expect(sections[0].text()).toContain("核心作用");
    expect(sections[1].text()).toContain("向下展开一层");
    expect(wrapper.find('[data-test="learning-keywords"]').exists()).toBe(false);
  });

  it("collapses usage and source details until the learner asks for them", () => {
    const wrapper = mount(AnnotationPanel);
    const usage = wrapper.get('[data-test="usage-example"]');
    const source = wrapper.get('[data-test="annotation-source"]');

    expect(usage.element.tagName).toBe("DETAILS");
    expect(usage.attributes("open")).toBeUndefined();
    expect(usage.text()).toContain("常见用法");
    expect(source.element.tagName).toBe("DETAILS");
    expect(source.attributes("open")).toBeUndefined();
    expect(source.text()).toContain("@Target({ElementType.TYPE})");
    expect(source.text()).toContain("源码拆解");
    expect(source.text()).toContain("只能标在类型上");
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
  });
});
