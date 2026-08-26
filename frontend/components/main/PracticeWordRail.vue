<template>
  <aside
    class="practice-word-rail flex h-full min-h-0 flex-col overflow-hidden border-r border-gray-200 pr-3 dark:border-gray-700"
    aria-label="本节练习内容"
    data-test="practice-word-rail"
  >
    <div class="word-rail-heading">
      <h5>本节内容</h5>
      <p>答对解锁 · 点击可看</p>
    </div>

    <div class="word-list flex min-h-0 flex-1 flex-col overflow-y-auto">
      <button
        v-for="statement in learningStatements"
        :key="statement.id"
        type="button"
        class="word-item flex h-16 shrink-0 items-center px-3"
        :class="{ 'is-current': isCurrentStatement(statement) }"
        :aria-label="isKeywordRevealed(statement) ? `${displayTerm(statement)} 已显示` : '显示这个练习词'"
        :aria-pressed="isKeywordRevealed(statement)"
        :data-active="isCurrentStatement(statement)"
        :data-revealed="isKeywordRevealed(statement)"
        data-test="learning-keyword"
        @click="revealKeyword(statement.id)"
      >
        <span
          class="word-content w-full"
          :class="{ 'is-blurred': !isKeywordRevealed(statement) }"
          data-test="keyword-content"
        >
          <span class="word-title-row">
            <span class="word-title">{{ displayTerm(statement) }}</span>
            <span
              v-if="courseStore.isStatementUnlocked(statement.id)"
              class="word-state is-unlocked"
            >已解锁</span>
            <span
              v-else-if="isCurrentStatement(statement)"
              class="word-state"
            >正在练</span>
          </span>
          <span class="word-meaning">{{ statement.chinese }}</span>
        </span>

        <span
          v-if="!isKeywordRevealed(statement)"
          class="word-lock"
        >
          <span>点击查看</span>
          <span>答对后自动解锁</span>
        </span>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";

import { useCourseStore } from "~/store/course";
import type { Statement } from "~/types";

const courseStore = useCourseStore();
const manuallyRevealedIds = ref<Set<string>>(new Set());

const learningStatements = computed(() => courseStore.currentCourse?.statements ?? []);

function isCurrentStatement(statement: Statement) {
  return statement.id === courseStore.currentStatement?.id;
}

function displayTerm(statement: Statement) {
  return `${statement.prefix ?? ""}${statement.english}`;
}

function isKeywordRevealed(statement: Statement) {
  return (
    courseStore.isStatementUnlocked(statement.id) || manuallyRevealedIds.value.has(statement.id)
  );
}

function revealKeyword(statementId: string) {
  if (courseStore.isStatementUnlocked(statementId)) return;
  manuallyRevealedIds.value = new Set(manuallyRevealedIds.value).add(statementId);
}

watch(
  () => courseStore.currentCourse?.id,
  () => {
    manuallyRevealedIds.value = new Set();
  },
);
</script>

<style scoped>
.practice-word-rail {
  width: 100%;
}

.word-rail-heading {
  @apply border-b border-gray-200 px-3 pb-2 dark:border-gray-700;
}

.word-rail-heading h5 {
  @apply text-xs font-semibold text-gray-600 dark:text-gray-300;
}

.word-rail-heading p {
  @apply mt-0.5 text-[9px] text-gray-400;
}

.word-list {
  @apply divide-y divide-gray-100 dark:divide-gray-800;
}

.word-item {
  @apply relative block w-full py-2.5 text-left transition-colors;
  min-height: 3.7rem;
}

.word-item:hover,
.word-item.is-current {
  @apply bg-fuchsia-50/60 dark:bg-fuchsia-950/20;
}

.word-item.is-current::before {
  @apply bg-fuchsia-500;
  position: absolute;
  top: 0.6rem;
  bottom: 0.6rem;
  right: -0.75rem;
  width: 2px;
  content: "";
}

.word-content,
.word-title-row,
.word-meaning {
  display: block;
}

.word-content {
  transition: filter 0.2s ease, opacity 0.2s ease;
}

.word-title-row {
  @apply flex items-center justify-between gap-1;
}

.word-title {
  @apply truncate font-mono text-xs font-semibold text-gray-700 dark:text-gray-200;
}

.word-state {
  @apply shrink-0 text-[9px] font-medium text-fuchsia-600 dark:text-fuchsia-300;
}

.word-state.is-unlocked {
  @apply text-green-600 dark:text-green-400;
}

.word-meaning {
  @apply mt-1 line-clamp-2 text-[10px] leading-4 text-gray-500 dark:text-gray-400;
}

.word-lock {
  @apply absolute inset-0 flex flex-col items-center justify-center bg-white/70 text-[10px] font-medium text-gray-500 backdrop-blur-[1px] dark:bg-theme-dark/70 dark:text-gray-300;
}

.word-lock span:last-child {
  @apply mt-0.5 text-[9px] font-normal opacity-70;
}

.is-blurred {
  filter: blur(5px);
  opacity: 0.4;
  user-select: none;
}
</style>
