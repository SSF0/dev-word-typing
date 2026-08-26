<template>
  <div class="usage-example-markdown">
    <template
      v-for="(block, index) in blocks"
      :key="`${block.type}-${index}`"
    >
      <p
        v-if="block.type === 'text'"
        class="usage-example-text"
      >
        {{ block.content }}
      </p>
      <pre v-else><code
        :class="`language-${block.language}`"
        :data-language="block.language"
      >{{ block.content }}</code></pre>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = withDefaults(defineProps<{
  source: string;
  defaultLanguage?: string;
}>(), {
  defaultLanguage: "text",
});

interface MarkdownBlock {
  type: "text" | "code";
  content: string;
  language: string;
}

const blocks = computed(() => parseUsageExample(props.source, props.defaultLanguage));

function parseUsageExample(source: string, defaultLanguage: string): MarkdownBlock[] {
  const fencePattern = /```([\w+-]*)\s*\n([\s\S]*?)```/g;
  const parsed: MarkdownBlock[] = [];
  let cursor = 0;
  let match: RegExpExecArray | null;
  let foundFence = false;

  while ((match = fencePattern.exec(source)) !== null) {
    foundFence = true;
    pushTextBlock(parsed, source.slice(cursor, match.index));
    parsed.push({
      type: "code",
      language: match[1] || defaultLanguage,
      content: match[2].replace(/\s+$/, ""),
    });
    cursor = fencePattern.lastIndex;
  }

  if (!foundFence) {
    return source.trim()
      ? [{ type: "code", language: defaultLanguage, content: source.trim() }]
      : [];
  }

  pushTextBlock(parsed, source.slice(cursor));

  return parsed;
}

function pushTextBlock(blocks: MarkdownBlock[], value: string) {
  const content = value.trim();
  if (!content) return;
  blocks.push({ type: "text", language: "text", content });
}
</script>

<style scoped>
.usage-example-markdown {
  @apply space-y-2;
}

.usage-example-text {
  @apply whitespace-pre-line text-[13px] leading-6 text-gray-600 dark:text-gray-300;
}

pre {
  @apply mb-3 overflow-x-auto rounded-sm bg-gray-900 p-3 text-xs leading-5 text-green-300;
}

code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
  white-space: pre;
}
</style>
