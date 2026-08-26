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
      <pre
        v-else
        :class="`language-${block.language}`"
      ><code
        :class="`language-${block.language}`"
        :data-language="block.language"
      ><span
        v-for="(token, tokenIndex) in block.tokens"
        :key="tokenIndex"
        :class="token.classes"
      >{{ token.content }}</span></code></pre>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import Prism from "prismjs";
import "prismjs/components/prism-java";
import "prismjs/components/prism-yaml";
import "prismjs/themes/prism-tomorrow.css";

Prism.manual = true;

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
  tokens: HighlightToken[];
}

interface HighlightToken {
  content: string;
  classes: string[];
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
    const language = match[1] || defaultLanguage;
    const content = match[2].replace(/\s+$/, "");
    parsed.push({
      type: "code",
      language,
      content,
      tokens: highlightCode(content, language),
    });
    cursor = fencePattern.lastIndex;
  }

  if (!foundFence) {
    const content = source.trim();
    return content
      ? [{
          type: "code",
          language: defaultLanguage,
          content,
          tokens: highlightCode(content, defaultLanguage),
        }]
      : [];
  }

  pushTextBlock(parsed, source.slice(cursor));

  return parsed;
}

function pushTextBlock(blocks: MarkdownBlock[], value: string) {
  const content = value.trim();
  if (!content) return;
  blocks.push({ type: "text", language: "text", content, tokens: [] });
}

function highlightCode(source: string, language: string): HighlightToken[] {
  const grammarName = normalizeLanguage(language);
  const grammar = Prism.languages[grammarName];
  if (!grammar) {
    return [{ content: source, classes: [] }];
  }

  return flattenTokenStream(Prism.tokenize(source, grammar));
}

function normalizeLanguage(language: string) {
  const normalized = language.toLowerCase();
  if (normalized === "xml" || normalized === "html") return "markup";
  if (normalized === "yml") return "yaml";
  return normalized;
}

function flattenTokenStream(
  stream: Prism.TokenStream,
  inheritedClasses: string[] = [],
): HighlightToken[] {
  const entries = Array.isArray(stream) ? stream : [stream];

  return entries.flatMap((entry) => {
    if (typeof entry === "string") {
      return [{ content: entry, classes: inheritedClasses }];
    }

    const aliases = Array.isArray(entry.alias) ? entry.alias : [entry.alias];
    const classes = Array.from(new Set([
      ...inheritedClasses,
      "token",
      entry.type,
      ...aliases.filter(Boolean),
    ]));
    return flattenTokenStream(entry.content, classes);
  });
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
  @apply mb-3 overflow-x-auto rounded-sm p-3 text-xs leading-5;
  background: #1f2937;
}

code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
  white-space: pre;
}
</style>
