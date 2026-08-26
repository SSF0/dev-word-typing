import { flushPromises, mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import AnnotationPanel from "../AnnotationPanel.vue";
import annotationPanelSource from "../AnnotationPanel.vue?raw";

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
          "```java\n@RestController // 声明 REST 控制器\npublic class UserController { // 定义用户控制器\n    public UserResponse detail() { return null; } // 查询用户详情\n}\n```",
        referenceCode: "@Controller\n@ResponseBody",
        note: "# 当前注解笔记\n\n- Controller 只调用 Service",
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
  beforeEach(() => {
    mocks.courseStore.currentStatement.note =
      "# 当前注解笔记\n\n- Controller 只调用 Service";
    mocks.updateStatementNote.mockReset();
  });

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

  it("opens usage and source details by default", () => {
    const wrapper = mount(AnnotationPanel);
    const usage = wrapper.get('[data-test="usage-example"]');
    const source = wrapper.get('[data-test="annotation-source"]');

    expect(usage.element.tagName).toBe("DETAILS");
    expect(usage.attributes("open")).toBe("");
    expect(usage.text()).toContain("public class UserController");
    expect(source.element.tagName).toBe("DETAILS");
    expect(source.attributes("open")).toBe("");
    expect(source.text()).toContain("@Controller");
    expect(source.text()).toContain("@ResponseBody");
  });

  it("right-aligns the disclosure action with space from the panel edge", () => {
    const wrapper = mount(AnnotationPanel);
    const usage = wrapper.get('[data-test="usage-example"]');
    const source = wrapper.get('[data-test="annotation-source"]');
    const summaryRule = annotationPanelSource.match(
      /\.annotation-details summary::after\s*\{[\s\S]*?\}/,
    )?.[0];

    expect(usage.get("summary").text()).toBe("使用示例");
    expect(usage.text()).not.toContain("需要时展开");
    expect(source.get("summary").text()).toContain("追根究底");
    expect(summaryRule).toContain("float: right");
    expect(summaryRule).toContain("margin-right: 0.75rem");
  });

  it("marks the 使用要点 heading with a star icon", () => {
    const wrapper = mount(AnnotationPanel);
    const usageTips = wrapper.findAll('[data-test="learning-section"]')[1];
    const icon = usageTips.get('svg[data-test="usage-tip-icon"]');

    expect(icon.attributes("aria-hidden")).toBe("true");
    expect(icon.classes()).toContain("text-yellow-400");
    expect(icon.classes()).not.toContain("text-fuchsia-500");
    expect(usageTips.get("h5").text()).toContain("使用要点");
  });

  it("aligns expanded content padding with the detail header", () => {
    const panelStyle = annotationPanelSource.match(
      /\.annotation-panel\s*\{[\s\S]*?\}/,
    )?.[0];
    const bodyStyle = annotationPanelSource.match(
      /\.annotation-body\s*\{[\s\S]*?\}/,
    )?.[0];
    const contentStyle = annotationPanelSource.match(
      /\.details-content\s*\{[\s\S]*?\}/,
    )?.[0];

    expect(panelStyle).toContain("px-4");
    expect(panelStyle).not.toContain("pl-4");
    expect(bodyStyle).not.toContain("pr-1");
    expect(contentStyle).toContain("pb-3");
    expect(contentStyle).not.toContain("pl-4");
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
    expect(code.text()).toContain("public class UserController { // 定义用户控制器");
  });

  it("applies distinct Java theme tokens to keywords, types, methods, and comments", () => {
    const wrapper = mount(AnnotationPanel);
    const code = wrapper.get('[data-test="usage-example"] code[data-language="java"]');

    expect(code.get(".token.keyword").text()).toBe("public");
    expect(code.findAll(".token.class-name").map((token) => token.text())).toContain(
      "UserController",
    );
    expect(code.get(".token.function").text()).toBe("detail");
    expect(code.get(".token.comment").text()).toContain("声明 REST 控制器");
  });

  it("renders reference source through the same Markdown code-block renderer", () => {
    const wrapper = mount(AnnotationPanel);
    const source = wrapper.get('[data-test="annotation-source"]');

    expect(source.find(".usage-example-markdown").exists()).toBe(true);
    const code = source.get('code[data-language="java"]');
    expect(code.classes()).toContain("language-java");
    expect(code.text()).toContain("@Controller");
    expect(code.text()).toContain("@ResponseBody");
  });

  it("renders a saved note as Markdown and lets the learner switch to editing", async () => {
    const wrapper = mount(AnnotationPanel);
    const body = wrapper.get(".annotation-body");
    const note = wrapper.get(".note-section");
    const preview = note.get('[data-test="note-preview"]');

    expect(body.element.lastElementChild).toBe(note.element);
    expect(note.classes()).toEqual(
      expect.arrayContaining(["flex", "min-h-0", "flex-1", "flex-col"]),
    );
    expect(preview.get("h1").text()).toBe("当前注解笔记");
    expect(preview.get("li").text()).toBe("Controller 只调用 Service");
    expect(note.find("textarea").exists()).toBe(false);

    await note.get('[data-test="edit-note"]').trigger("click");

    const textarea = note.get("textarea");
    expect(textarea.classes()).toEqual(
      expect.arrayContaining(["min-h-28", "flex-1", "resize-none"]),
    );
    expect((textarea.element as HTMLTextAreaElement).value).toBe(
      "# 当前注解笔记\n\n- Controller 只调用 Service",
    );
    expect(note.get('[data-test="save-note"]').classes()).toContain("flex-1");
  });

  it("imports a Markdown file into the note editor", async () => {
    const wrapper = mount(AnnotationPanel);
    await wrapper.get('[data-test="edit-note"]').trigger("click");
    const input = wrapper.get<HTMLInputElement>('input[type="file"]');
    const file = new File(["## 导入的理解\n\n`@Valid` 触发校验"], "valid-note.md", {
      type: "text/markdown",
    });
    Object.defineProperty(file, "text", {
      value: vi.fn().mockResolvedValue("## 导入的理解\n\n`@Valid` 触发校验"),
    });
    Object.defineProperty(input.element, "files", {
      configurable: true,
      value: [file],
    });

    await input.trigger("change");
    await flushPromises();

    expect((wrapper.get("textarea").element as HTMLTextAreaElement).value).toBe(
      "## 导入的理解\n\n`@Valid` 触发校验",
    );
  });

  it("escapes raw HTML while rendering a Markdown note", () => {
    mocks.courseStore.currentStatement.note =
      "<script>alert('note')</script>\n\n**安全显示**";

    const wrapper = mount(AnnotationPanel);
    const preview = wrapper.get('[data-test="note-preview"]');

    expect(preview.find("script").exists()).toBe(false);
    expect(preview.get("strong").text()).toBe("安全显示");
  });

  it("does not draw another left border inside the detached detail panel", () => {
    const styleBlock = annotationPanelSource.match(
      /\.annotation-panel\s*\{[\s\S]*?\}/,
    )?.[0];

    expect(styleBlock).toBeDefined();
    expect(styleBlock).not.toContain("border-l");
    expect(styleBlock).not.toContain("dark:border-gray-700");
  });

  it("saves a note on the current learning item", async () => {
    mocks.updateStatementNote.mockResolvedValue({
      ...mocks.courseStore.currentStatement,
      note: "新的理解",
    });
    const wrapper = mount(AnnotationPanel);

    await wrapper.get('[data-test="edit-note"]').trigger("click");
    await wrapper.get("textarea").setValue("新的理解");
    await wrapper.get('[data-test="save-note"]').trigger("click");
    await flushPromises();

    expect(mocks.updateStatementNote).toHaveBeenCalledWith(
      "pack-1",
      "course-1",
      "statement-1",
      "新的理解",
    );
    expect(wrapper.find("textarea").exists()).toBe(false);
    expect(wrapper.get('[data-test="note-preview"]').text()).toContain("新的理解");
  });
});
