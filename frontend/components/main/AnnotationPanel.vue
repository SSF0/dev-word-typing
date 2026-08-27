<template>
  <div class="annotation-panel">
    <header class="annotation-header pt-4">
      <div class="min-w-0">
        <div
          class="mb-0.5 text-base text-gray-400"
          data-test="detail-course-title"
        >
          {{ courseStore.currentCourse?.title }}
        </div>
        <div class="flex items-center gap-2">
          <h4
            class="truncate text-sm font-bold text-gray-700 dark:text-gray-300"
            data-test="detail-term"
          >
            {{ currentItem ? `${currentItem.prefix ?? ""}${currentItem.english}` : "" }}
          </h4>
          <span
            class="badge badge-sm"
            :class="isWordMode ? 'badge-primary' : 'badge-outline'"
          >
            {{ isWordMode ? "单词" : "整句" }}
          </span>
        </div>
      </div>
    </header>

    <div class="annotation-body">
      <section
        v-if="conceptSections.length"
        class="learning-guide"
        data-test="learning-guide"
      >
        <article
          v-for="(section, index) in conceptSections"
          :key="section.title"
          class="learning-section"
          :class="{ 'is-primary': index === 0 }"
          data-test="learning-section"
        >
          <h5 class="flex items-center gap-1.5">
            <svg
              v-if="section.title === '使用要点'"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-4 w-4 shrink-0 text-yellow-400"
              data-test="usage-tip-icon"
              aria-hidden="true"
            >
              <path d="m12 2.5 2.94 5.96 6.58.96-4.76 4.64 1.12 6.55L12 17.52l-5.88 3.09 1.12-6.55-4.76-4.64 6.58-.96L12 2.5Z" />
            </svg>
            <span>{{ section.title }}</span>
          </h5>
          <p>{{ section.body }}</p>
        </article>
      </section>

      <details
        v-if="currentItem?.usageExample"
        open
        class="annotation-details"
        data-test="usage-example"
      >
        <summary>
          <span>使用示例</span>
        </summary>
        <div class="details-content source-details">
          <UsageExampleMarkdown :source="currentItem.usageExample" />
        </div>
      </details>

      <details
        v-if="currentItem?.referenceCode"
        open
        class="annotation-details"
        data-test="annotation-source"
      >
        <summary>
          <span>参考源码与实现</span>
          <span class="summary-hint">追根究底</span>
        </summary>
        <div class="details-content source-details">
          <UsageExampleMarkdown
            :source="currentItem.referenceCode"
            default-language="java"
          />
        </div>
      </details>

      <section class="note-section flex min-h-0 flex-1 flex-col">
        <div class="mb-2 flex shrink-0 items-center justify-between gap-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400">
            我的笔记 / 见解
          </div>
          <button
            v-if="hasSavedNote && !editingNote"
            class="btn btn-ghost btn-xs"
            type="button"
            data-test="edit-note"
            @click="startEditingNote"
          >
            修改笔记
          </button>
        </div>

        <div
          v-if="!editingNote"
          class="note-preview min-h-28 flex-1 overflow-auto rounded-sm border border-gray-200 p-3 dark:border-gray-700"
          data-test="note-preview"
        >
          <NoteMarkdown :source="savedNote" />
        </div>

        <template v-else>
          <textarea
            v-model="noteDraft"
            rows="2"
            class="textarea textarea-bordered min-h-28 w-full flex-1 resize-none text-xs leading-5"
            placeholder="支持 Markdown，可直接记录标题、列表、代码块和心得..."
          ></textarea>
          <div class="mt-2 flex shrink-0 items-center gap-2">
            <label class="btn btn-outline btn-sm cursor-pointer">
              导入 .md
              <input
                class="sr-only"
                type="file"
                accept=".md,text/markdown,text/plain"
                @change="importMarkdownNote"
              />
            </label>
            <button
              v-if="hasSavedNote"
              class="btn btn-ghost btn-sm"
              type="button"
              data-test="cancel-note-edit"
              @click="cancelEditingNote"
            >
              取消
            </button>
            <button
              class="btn btn-primary btn-sm min-w-0 flex-1"
              type="button"
              data-test="save-note"
              :disabled="saving"
              @click="saveNote"
            >
              {{ saving ? "保存中..." : "保存笔记" }}
            </button>
          </div>
        </template>
        <div
          v-if="saved"
          class="mt-1 shrink-0 text-right text-xs text-green-500"
        >
          已保存
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { toast } from "vue-sonner";

import { updateStatementNote } from "~/api/course";
import { useCourseStore } from "~/store/course";
import NoteMarkdown from "./NoteMarkdown.vue";
import UsageExampleMarkdown from "./UsageExampleMarkdown.vue";

const courseStore = useCourseStore();
const noteDraft = ref("");
const saving = ref(false);
const saved = ref(false);
const editingNote = ref(true);
const persistedNote = ref("");

const isWordMode = computed(() => courseStore.currentCourse?.practiceType === "WORD");
const currentItem = computed(() => courseStore.currentStatement);
const conceptSections = computed(() => parseGuide(currentItem.value?.explanation));
const savedNote = computed(() => persistedNote.value.trim());
const hasSavedNote = computed(() => savedNote.value.length > 0);

interface GuideSection {
  title: string;
  body: string;
}

function parseGuide(value?: string): GuideSection[] {
  const guide = value?.trim();
  if (!guide) return [];

  if (!guide.startsWith("## ")) {
    return [{ title: "快速理解", body: guide }];
  }

  return guide
    .split(/^##\s+/m)
    .filter(Boolean)
    .map((part) => {
      const [title, ...body] = part.trim().split("\n");
      return { title: title.trim(), body: body.join("\n").trim() };
    });
}

watch(
  () => courseStore.currentStatement?.id,
  () => {
    persistedNote.value = courseStore.currentStatement?.note ?? "";
    noteDraft.value = persistedNote.value;
    editingNote.value = !noteDraft.value.trim();
    saved.value = false;
  },
  { immediate: true },
);

function startEditingNote() {
  noteDraft.value = persistedNote.value;
  editingNote.value = true;
  saved.value = false;
}

function cancelEditingNote() {
  noteDraft.value = persistedNote.value;
  editingNote.value = false;
  saved.value = false;
}

async function importMarkdownNote(event: Event) {
  const input = event.currentTarget as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  const isMarkdown = file.name.toLowerCase().endsWith(".md")
    || file.type === "text/markdown"
    || file.type === "text/plain";

  if (!isMarkdown) {
    toast.error("请选择 Markdown 文件（.md）");
    input.value = "";
    return;
  }

  try {
    noteDraft.value = await file.text();
    saved.value = false;
  } catch {
    toast.error("读取 Markdown 文件失败");
  } finally {
    input.value = "";
  }
}

async function saveNote() {
  const course = courseStore.currentCourse;
  const statement = courseStore.currentStatement;
  if (!course || !statement) return;

  saving.value = true;
  try {
    const updated = await updateStatementNote(
      course.coursePackId,
      course.id,
      statement.id,
      noteDraft.value,
    );
    if (courseStore.currentStatement?.id === statement.id) {
      courseStore.currentStatement.note = updated.note;
    }
    noteDraft.value = updated.note ?? noteDraft.value;
    persistedNote.value = noteDraft.value;
    editingNote.value = !noteDraft.value.trim();
    saved.value = true;
    toast.success("笔记已保存");
  } catch {
    toast.error("保存失败，请重试");
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.annotation-panel {
  @apply flex h-full flex-col bg-white px-4 dark:bg-theme-dark;
  min-height: 0;
  overflow: hidden;
}

.annotation-header {
  @apply flex shrink-0 items-center justify-between border-b border-gray-200 pb-3 dark:border-gray-700;
}

.annotation-body {
  @apply flex min-h-0 flex-1 flex-col overflow-y-auto overflow-x-hidden;
}

.learning-guide,
.annotation-details {
  flex-shrink: 0;
}

.learning-section {
  @apply border-b border-gray-200 py-3.5 dark:border-gray-700;
}

.learning-section.is-primary {
  @apply border-l-2 border-l-fuchsia-400 pl-3;
}

.learning-section h5 {
  @apply mb-1.5 text-sm font-semibold text-gray-700 dark:text-gray-200;
}

.learning-section.is-primary h5 {
  @apply text-fuchsia-700 dark:text-fuchsia-300;
}

.learning-section p,
.details-content p {
  @apply whitespace-pre-line text-[13px] leading-6 text-gray-600 dark:text-gray-300;
}

.annotation-details {
  @apply border-b border-gray-200 dark:border-gray-700;
}

.annotation-details summary {
  @apply cursor-pointer select-none py-3 text-sm font-semibold text-gray-700 marker:text-gray-400 hover:text-fuchsia-600 dark:text-gray-200 dark:hover:text-fuchsia-300;
}

.annotation-details summary::after {
  content: "展开";
  float: right;
  margin-right: 0.75rem;
  color: rgb(156 163 175);
  font-size: 0.65rem;
  font-weight: 400;
}

.annotation-details[open] summary::after {
  content: "收起";
}

.summary-hint {
  @apply ml-2 text-[10px] font-normal text-gray-400;
}

.details-content {
  @apply pb-3;
}

.details-content h6 {
  @apply mb-1 text-xs font-semibold text-fuchsia-600 dark:text-fuchsia-300;
}

.source-explanation {
  @apply border-l border-gray-200 pl-3 dark:border-gray-700;
}

.note-section {
  @apply pt-3;
}
</style>
