<template>
  <div
    class="note-markdown"
    v-html="renderedNote"
  ></div>
</template>

<script setup lang="ts">
import MarkdownIt from "markdown-it";
import { computed } from "vue";

const props = defineProps<{
  source: string;
}>();

const markdown = new MarkdownIt({
  breaks: true,
  html: false,
  linkify: true,
});

const renderedNote = computed(() => markdown.render(props.source));
</script>

<style scoped>
.note-markdown {
  @apply break-words text-[13px] leading-6 text-gray-600 dark:text-gray-300;
}

.note-markdown :deep(h1),
.note-markdown :deep(h2),
.note-markdown :deep(h3) {
  @apply mb-2 mt-4 font-semibold leading-6 text-gray-800 first:mt-0 dark:text-gray-100;
}

.note-markdown :deep(h1) {
  @apply text-lg;
}

.note-markdown :deep(h2) {
  @apply text-base;
}

.note-markdown :deep(h3) {
  @apply text-sm;
}

.note-markdown :deep(p) {
  @apply mb-2 last:mb-0;
}

.note-markdown :deep(ul) {
  @apply mb-2 list-disc space-y-1 pl-5;
}

.note-markdown :deep(ol) {
  @apply mb-2 list-decimal space-y-1 pl-5;
}

.note-markdown :deep(blockquote) {
  @apply my-2 border-l-2 border-fuchsia-300 pl-3 text-gray-500 dark:border-fuchsia-700 dark:text-gray-400;
}

.note-markdown :deep(pre) {
  @apply my-2 overflow-x-auto rounded-sm bg-gray-900 p-3 text-xs leading-5 text-gray-100;
}

.note-markdown :deep(code) {
  @apply rounded bg-gray-100 px-1 py-0.5 font-mono text-[0.92em] text-fuchsia-700 dark:bg-gray-800 dark:text-fuchsia-300;
}

.note-markdown :deep(pre code) {
  @apply bg-transparent p-0 text-inherit;
}

.note-markdown :deep(a) {
  @apply text-fuchsia-600 underline underline-offset-2 dark:text-fuchsia-300;
}

.note-markdown :deep(hr) {
  @apply my-3 border-gray-200 dark:border-gray-700;
}
</style>
