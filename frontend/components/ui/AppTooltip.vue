<template>
  <span
    class="group relative inline-flex"
    :title="text"
  >
    <slot />
    <span
      class="pointer-events-none absolute bottom-full left-1/2 z-50 mb-2 hidden -translate-x-1/2 whitespace-nowrap rounded bg-slate-900 px-2 py-1 text-xs text-white shadow-lg group-hover:block group-focus-within:block dark:bg-slate-100 dark:text-slate-900"
      role="tooltip"
    >
      {{ text }}
      <span v-if="shortcuts?.length">（{{ shortcuts.join(" + ") }}）</span>
    </span>
  </span>
</template>

<script setup lang="ts">
defineProps<{
  text: string;
  shortcuts?: string[];
}>();
</script>
