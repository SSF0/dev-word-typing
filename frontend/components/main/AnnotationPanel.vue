<template>
  <div class="annotation-panel">
    <header class="annotation-header">
      <div class="min-w-0">
        <div class="mb-0.5 text-[10px] text-gray-400">
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
      <button
        class="btn btn-ghost btn-xs"
        type="button"
        aria-label="收起知识点详情"
        @click="emit('close')"
      >
        收起详情
      </button>
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
          <h5>{{ section.title }}</h5>
          <p>{{ section.body }}</p>
        </article>
      </section>

      <details
        v-if="currentItem?.usageExample"
        class="annotation-details"
        data-test="usage-example"
      >
        <summary>
          <span>使用示例</span>
          <span class="summary-hint">需要时展开</span>
        </summary>
        <div class="details-content source-details">
          <UsageExampleMarkdown :source="currentItem.usageExample" />
        </div>
      </details>

      <details
        v-if="currentItem?.referenceCode"
        class="annotation-details"
        data-test="annotation-source"
      >
        <summary>
          <span>参考源码与实现</span>
          <span class="summary-hint">追根究底</span>
        </summary>
        <div class="details-content source-details">
          <pre><code>{{ currentItem.referenceCode }}</code></pre>
        </div>
      </details>

      <section class="note-section flex min-h-0 flex-1 flex-col">
        <div class="mb-1 shrink-0 text-xs font-semibold text-gray-500 dark:text-gray-400">
          我的笔记 / 见解
        </div>
        <textarea
          v-model="noteDraft"
          rows="2"
          class="textarea textarea-bordered min-h-28 w-full flex-1 resize-none text-xs"
          placeholder="记录你对这个单词的理解、踩坑或心得..."
        ></textarea>
        <div class="mt-2 flex shrink-0 flex-col gap-1.5">
          <span
            v-if="saved"
            class="self-end text-xs text-green-500"
          >已保存</span>
          <button
            class="btn btn-primary btn-sm w-full"
            :disabled="saving"
            @click="saveNote"
          >
            {{ saving ? "保存中..." : "保存笔记" }}
          </button>
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
import UsageExampleMarkdown from "./UsageExampleMarkdown.vue";

const emit = defineEmits<{
  close: [];
}>();

const courseStore = useCourseStore();
const noteDraft = ref("");
const saving = ref(false);
const saved = ref(false);

const isWordMode = computed(() => courseStore.currentCourse?.practiceType === "WORD");
const currentItem = computed(() => courseStore.currentStatement);
const conceptSections = computed(() => parseGuide(currentItem.value?.explanation));

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
    noteDraft.value = courseStore.currentStatement?.note ?? "";
    saved.value = false;
  },
  { immediate: true },
);

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
  @apply flex h-full flex-col border-l border-gray-200 bg-white pl-4 dark:border-gray-700 dark:bg-theme-dark;
  min-height: 0;
  overflow: hidden;
}

.annotation-header {
  @apply flex shrink-0 items-center justify-between border-b border-gray-200 pb-3 dark:border-gray-700;
}

.annotation-body {
  @apply flex min-h-0 flex-1 flex-col overflow-y-auto overflow-x-hidden pr-1;
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
  margin-left: 0.5rem;
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
  @apply pb-3 pl-4;
}

.details-content h6 {
  @apply mb-1 text-xs font-semibold text-fuchsia-600 dark:text-fuchsia-300;
}

.source-details pre {
  @apply mb-3 overflow-x-auto bg-gray-900 p-3 text-xs leading-5 text-green-300;
}

.source-explanation {
  @apply border-l border-gray-200 pl-3 dark:border-gray-700;
}

.note-section {
  @apply pt-3;
}
</style>
