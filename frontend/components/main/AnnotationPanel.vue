<template>
  <div class="annotation-panel">
    <div class="mb-2 flex items-center justify-between">
      <div class="flex items-center gap-2">
        <h4 class="text-sm font-bold text-gray-700 dark:text-gray-300">
          {{ courseStore.currentCourse?.title }}
        </h4>
        <span
          class="badge badge-sm"
          :class="isWordMode ? 'badge-primary' : 'badge-outline'"
        >
          {{ isWordMode ? "单词" : "整句" }}
        </span>
      </div>
      <button
        class="btn btn-ghost btn-xs"
        @click="collapsed = !collapsed"
      >
        {{ collapsed ? "展开" : "收起" }}
      </button>
    </div>

    <div v-if="!collapsed">
      <pre
        v-if="courseStore.currentCourse?.annotationCode"
        class="overflow-x-auto rounded-lg bg-gray-900 p-3 text-xs leading-relaxed text-green-300"
    ><code>{{ courseStore.currentCourse?.annotationCode }}</code></pre>
      <p
        v-else
        class="text-xs text-gray-400"
      >暂无源码。</p>

      <div
        v-if="courseStore.currentCourse?.annotationExplain"
        class="mt-3 rounded-md border border-gray-200 p-2 text-xs leading-relaxed text-gray-600 dark:border-gray-700 dark:text-gray-300"
      >
        {{ courseStore.currentCourse.annotationExplain }}
      </div>

      <!-- 个人笔记 -->
      <div class="mt-4">
        <div class="mb-1 text-xs font-semibold text-gray-500 dark:text-gray-400">
          我的笔记 / 见解
        </div>
        <textarea
          v-model="noteDraft"
          rows="3"
          class="textarea textarea-bordered w-full text-xs"
          placeholder="记录你对这个知识点的理解、踩坑或心得..."
        ></textarea>
        <div class="mt-1.5 flex items-center justify-end gap-2">
          <span
            v-if="saved"
            class="text-xs text-green-500"
          >已保存</span>
          <button
            class="btn btn-primary btn-xs"
            :disabled="saving"
            @click="saveNote"
          >
            {{ saving ? "保存中..." : "保存笔记" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { toast } from "vue-sonner";

import { updateCourseNote } from "~/api/course";
import { useCourseStore } from "~/store/course";

const courseStore = useCourseStore();
const collapsed = ref(false);
const noteDraft = ref("");
const saving = ref(false);
const saved = ref(false);

const isWordMode = computed(() => courseStore.currentCourse?.practiceType === "WORD");

watch(
  () => courseStore.currentCourse?.note,
  (n) => {
    noteDraft.value = n ?? "";
    saved.value = false;
  },
  { immediate: true },
);

async function saveNote() {
  const c = courseStore.currentCourse;
  if (!c) return;
  saving.value = true;
  try {
    const updated = await updateCourseNote(c.coursePackId, c.id, noteDraft.value);
    if (courseStore.currentCourse) {
      courseStore.currentCourse.note = updated.note;
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
  @apply rounded-xl border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-900;
}
</style>